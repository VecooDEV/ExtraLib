const fs = require('fs');
const path = require('path');
const {spawn} = require('child_process');
const protocol = require('minecraft-protocol');
const minecraftData = require('minecraft-data');

const root = path.resolve(__dirname, '../..');
const modules = ['forge20', 'fabric20', 'fabric21', 'neoforge21', 'fabric261', 'neoforge261'];

function prepare(moduleName) {
    const profile = path.join(root, moduleName, 'run/functional');
    fs.mkdirSync(profile, {recursive: true});
    const eula = path.join(profile, 'eula.txt');

    if (!fs.existsSync(eula)) fs.writeFileSync(eula, 'eula=false\n');
    if (!fs.existsSync(path.join(profile, 'server.properties'))) {
        fs.writeFileSync(path.join(profile, 'server.properties'), 'server-ip=127.0.0.1\nserver-port=25580\nonline-mode=false\nview-distance=2\nsimulation-distance=2\nlevel-type=minecraft:flat\nlevel-name=functional-world\nspawn-protection=0\nmax-players=2\nenable-status=false\ndifficulty=peaceful\n');
    }
    if (!/^eula=true\s*$/m.test(fs.readFileSync(eula, 'utf8'))) {
        throw new Error(`Accept the Minecraft EULA in ${eula} before running the test.`);
    }

    return profile;
}

async function run(moduleName) {
    const profile = prepare(moduleName);
    const resultFile = path.join(profile, 'functional-results.txt');
    if (fs.existsSync(resultFile)) fs.unlinkSync(resultFile);
    const output = fs.createWriteStream(path.join(profile, 'functional-test.log'));
    const args = ['-I', 'tests/functional/functional-tests.init.gradle', `-PfunctionalLoader=${moduleName}`, `:${moduleName}:runServer`, '--console=plain'];
    const windows = process.platform === 'win32';
    const child = spawn(windows ? (process.env.ComSpec || 'cmd.exe') : './gradlew', windows ? ['/d', '/s', '/c', `gradlew.bat ${args.join(' ')}`] : args, {
        cwd: root,
        windowsHide: true,
        env: process.env,
        stdio: ['pipe', 'pipe', 'pipe']
    });
    const metrics = {windows: [], buttonClicks: 0, displayedButton: false, lockedCloseAttempts: 0, lockedReopens: 0, packetMessages: []};
    let buffer = '', client, complete = false, stopAt;
    let buttonsId;
    const timers = new Set();

    function later(callback, delay) {
        const timer = setTimeout(() => {
            timers.delete(timer);
            if (client && !client.ended) callback();
        }, delay);
        timers.add(timer);
    }

    function stop() {
        if (!stopAt) stopAt = Date.now();
        child.stdin.write('stop\n');
    }

    function connect() {
        const legacy = moduleName.endsWith('20');
        const version = moduleName.endsWith('261') ? '26.1' : legacy ? '1.20.1' : '1.21.1';
        const data = minecraftData(version);
        const emptySlot = moduleName.endsWith('261') ? null : legacy ? {present: false} : {itemCount: 0};
        const states = new Map();
        client = protocol.createClient({host: '127.0.0.1', port: 25580, username: 'ExtraLibTest', auth: 'offline', version, hideErrors: true});
        client.on('custom_payload', packet => {
            if (packet.channel === 'extralib:functional') {
                metrics.packetMessages.push(packet.data.subarray(1, 1 + packet.data[0]).toString('utf8'));
            }
        });
        client.on('ping', packet => client.write('pong', {id: packet.id}));
        client.on('error', error => {
            console.error('Test client: ' + error.message);
            if (!complete) stop();
        });
        client.on('end', () => {
            if (!complete) stop();
        });
        client.on('window_items', packet => {
            states.set(packet.windowId, packet.stateId);
            const first = packet.items[0];
            if (packet.windowId === buttonsId && first && first.itemId === data.itemsByName.diamond.id) {
                metrics.displayedButton = true;
            }
        });
        client.on('set_slot', packet => states.set(packet.windowId, packet.stateId));
        client.on('open_window', packet => {
            const title = JSON.stringify(packet.windowTitle);
            const id = packet.windowId;
            metrics.windows.push(title);
            if (title.includes('Functional/buttons')) {
                buttonsId = id;
                const actions = [
                    {slot: 0, mouseButton: 0, mode: 0},
                    {slot: 0, mouseButton: 1, mode: 0},
                    {slot: 0, mouseButton: 0, mode: 1},
                    {slot: 0, mouseButton: 1, mode: 2},
                    {slot: 0, mouseButton: 0, mode: 4},
                    {slot: 1, mouseButton: 0, mode: 0}
                ];
                actions.forEach((action, index) => later(() => {
                    client.write('window_click', {windowId: id, stateId: states.get(id) || 0, ...action, changedSlots: [], cursorItem: emptySlot});
                    metrics.buttonClicks++;
                }, 100 + index * 80));
                later(() => client.write('close_window', {windowId: id}), 750);
            } else if (title.includes('Functional/locked')) {
                if (metrics.lockedCloseAttempts === 0) {
                    later(() => {
                        metrics.lockedCloseAttempts++;
                        client.write('close_window', {windowId: id});
                    }, 100);
                } else {
                    metrics.lockedReopens++;
                }
            } else if (title.includes('Functional/deferred-first')) {
                later(() => client.write('close_window', {windowId: id}), 150);
            } else if (title.includes('Functional/deferred-second')) {
                later(() => client.write('close_window', {windowId: id}), 400);
            }
        });
    }

    function receive(bytes) {
        output.write(bytes);
        buffer = (buffer + bytes.toString()).slice(-32000);
        if (!client && /Done \([\d.,]+s\)!/.test(buffer)) {
            console.log(moduleName + ': connecting test player');
            connect();
        }
        if (/FUNCTIONAL_TEST_RESULT/.test(buffer)) {
            complete = true;
            if (!stopAt) stopAt = Date.now();
        }
        for (const line of bytes.toString().split(/\r?\n/)) {
            if (/FUNCTIONAL_TEST|error:|FAILED/.test(line)) console.log(line);
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
            timers.forEach(clearTimeout);
            if (client) client.end();
            output.end();
            const result = fs.existsSync(resultFile) ? fs.readFileSync(resultFile, 'utf8') : 'no result';
            const packetsPassed = moduleName !== 'fabric20' || JSON.stringify(metrics.packetMessages) === JSON.stringify(['player', 'generic', 'uuid', 'name', 'all', 'dimension', 'first', 'second']);
            const networkPassed = packetsPassed && metrics.buttonClicks === 6 && metrics.displayedButton && metrics.lockedCloseAttempts === 1 && metrics.lockedReopens >= 1 &&
                    metrics.windows.some(title => title.includes('Functional/deferred-second'));
            const passed = code === 0 && complete && result.startsWith('passed=true') && networkPassed;
            const summary = {moduleName, passed, code, networkPassed, shutdownSeconds: stopAt ? (Date.now() - stopAt) / 1000 : null, metrics, result};
            fs.writeFileSync(path.join(profile, 'functional-summary.json'), JSON.stringify(summary, null, 2));
            console.log(moduleName + ': ' + JSON.stringify(summary));
            resolve(passed);
        });
    });
}

(async () => {
    const target = process.argv[2];
    if (target !== 'all' && !modules.includes(target)) throw new Error('Usage: node tests/functional/run.cjs <all|forge20|fabric20|fabric21|neoforge21|fabric261|neoforge261>');
    let passed = true;
    for (const moduleName of target === 'all' ? modules : [target]) passed = await run(moduleName) && passed;
    process.exitCode = passed ? 0 : 1;
})().catch(error => {
    console.error(error);
    process.exitCode = 1;
});

