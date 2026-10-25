const fs = require('fs');
const path = require('path');
const {spawn} = require('child_process');
const protocol = require('minecraft-protocol');
const {prepare} = require('./prepare-profiles.cjs');

const root = path.resolve(__dirname, '../..');

async function run(moduleName, withLuckPerms) {
    const profile = await prepare(root, moduleName, withLuckPerms);
    const name = `${moduleName}-${withLuckPerms ? 'luckperms' : 'vanilla'}`;
    const resultFile = path.join(profile, 'permission-results.txt');
    if (fs.existsSync(resultFile)) fs.unlinkSync(resultFile);
    const output = fs.createWriteStream(path.join(profile, 'permission-test.log'));
    const args = ['-I', 'tests/permissions/permission-tests.init.gradle', `-PpermissionLoader=${moduleName}`, `-PpermissionLuckPerms=${withLuckPerms}`, `:${moduleName}:runServer`, '--console=plain'];
    const windows = process.platform === 'win32';
    const child = spawn(windows ? (process.env.ComSpec || 'cmd.exe') : './gradlew', windows ? ['/d', '/s', '/c', `gradlew.bat ${args.join(' ')}`] : args, {
        cwd: root,
        windowsHide: true,
        env: process.env,
        stdio: ['pipe', 'pipe', 'pipe']
    });
    let buffer = '', client, complete = false, stopAt;

    function stop() {
        if (!stopAt) stopAt = Date.now();
        child.stdin.write('stop\n');
    }

    function receive(data) {
        output.write(data);
        buffer = (buffer + data.toString()).slice(-32000);
        if (!client && /Done \([\d.,]+s\)!/.test(buffer)) {
            console.log(`${name}: server ready, connecting test player`);
            client = protocol.createClient({host: '127.0.0.1', port: 25578, username: 'ExtraPermTest', auth: 'offline', version: '1.21.1', hideErrors: true});
            client.on('ping', packet => client.write('pong', {id: packet.id}));
            client.on('error', error => {
                console.error(`Test client: ${error.message}`);
                if (!complete) stop();
            });
            client.on('end', () => {
                if (!complete) stop();
            });
        }
        if (/PERMISSION_TEST_RESULT/.test(buffer)) {
            complete = true;
            if (!stopAt) stopAt = Date.now();
        }
        for (const line of data.toString().split(/\r?\n/)) {
            if (/PERMISSION_TEST|error:|FAILED|Successfully initialized permission handler/.test(line)) console.log(line);
        }
    }

    child.stdout.on('data', receive);
    child.stderr.on('data', receive);
    child.stdin.on('error', () => {});
    const watchdog = setTimeout(stop, 180000);
    watchdog.unref();
    return new Promise((resolve, reject) => {
        child.on('error', error => {
            clearTimeout(watchdog);
            output.end();
            reject(error);
        });
        child.on('exit', code => {
            clearTimeout(watchdog);
            if (client) client.end();
            output.end();
            const result = fs.existsSync(resultFile) ? fs.readFileSync(resultFile, 'utf8') : 'no result';
            const passed = code === 0 && complete && result.startsWith('passed=true');
            console.log(`${name}: passed=${passed}, shutdown=${stopAt ? ((Date.now() - stopAt) / 1000).toFixed(2) + 's' : 'n/a'}`);
            console.log(result);
            resolve(passed);
        });
    });
}

(async () => {
    const moduleName = process.argv[2];
    let passed = true;
    if (moduleName === 'all') {
        for (const module of ['fabric21', 'neoforge21']) {
            for (const luckPerms of [false, true]) passed = await run(module, luckPerms) && passed;
        }
    } else {
        if (!['fabric21', 'neoforge21'].includes(moduleName) || !['false', 'true'].includes(process.argv[3])) {
            throw new Error('Usage: node tests/permissions/run.cjs all | <fabric21|neoforge21> <false|true>');
        }
        passed = await run(moduleName, process.argv[3] === 'true');
    }
    process.exitCode = passed ? 0 : 1;
})().catch(error => {
    console.error(error);
    process.exitCode = 1;
});
