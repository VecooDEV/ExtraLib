const fs = require('fs');
const path = require('path');
const https = require('https');
const crypto = require('crypto');

const dependencies = {
    fabric20: {
        filename: 'LuckPerms-Fabric-5.4.102.jar',
        url: 'https://cdn.modrinth.com/data/Vebnzrzj/versions/7PNj6nCm/LuckPerms-Fabric-5.4.102.jar',
        sha512: '45058fd2bbd2e7723af01a093ef7233d8a6fa2278a2232dfcc2aa53dae6df7ed78dbfbdb9ee133b19bc36655ccff5a4facdd476319c18e290b284733bf10edff'
    },
    fabric21: {
        filename: 'LuckPerms-Fabric-5.4.140.jar',
        url: 'https://cdn.modrinth.com/data/Vebnzrzj/versions/l47d4ZWk/LuckPerms-Fabric-5.4.140.jar',
        sha512: '386ff4d18981c830d21ddaa599d70c1c5430c1475e2c5960275bad535cbd572c77e38b3841f0a71e3ea4a795c56d45ff8c4a8dba3799dae26bff5a382c6e0a64'
    },
    neoforge21: {
        filename: 'LuckPerms-NeoForge-5.4.150.jar',
        url: 'https://cdn.modrinth.com/data/Vebnzrzj/versions/dKAPoPGd/LuckPerms-NeoForge-5.4.150.jar',
        sha512: '26de0e74d87b91acf3b71050ed1880a50ffd1809b3a1309a2e24bee5413193f27a5056e13b69d8f39085e28711244c02bc5fc329be13174abb520008a0ca9682'
    }
};

function download(url, redirects = 0) {
    return new Promise((resolve, reject) => {
        https.get(url, {headers: {'User-Agent': 'ExtraLib-permission-integration-tests'}}, response => {
            if (response.statusCode >= 300 && response.statusCode < 400 && redirects < 3) {
                response.resume();
                return download(new URL(response.headers.location, url).href, redirects + 1).then(resolve, reject);
            }
            if (response.statusCode !== 200) {
                response.resume();
                return reject(new Error(`Download failed: HTTP ${response.statusCode}`));
            }
            const chunks = [];
            response.on('data', chunk => chunks.push(chunk));
            response.on('end', () => resolve(Buffer.concat(chunks)));
            response.on('error', reject);
        }).on('error', reject);
    });
}

async function prepare(root, moduleName, withLuckPerms) {
    const dependency = dependencies[moduleName];
    if (!dependency) throw new Error('Supported modules: fabric20, fabric21, neoforge21');
    const base = path.join(root, moduleName, 'run/permissions');
    const dependencyDir = path.join(base, 'dependencies');
    fs.mkdirSync(dependencyDir, {recursive: true});
    const jar = path.join(dependencyDir, dependency.filename);
    const bytes = fs.existsSync(jar) ? fs.readFileSync(jar) : await download(dependency.url);
    if (crypto.createHash('sha512').update(bytes).digest('hex') !== dependency.sha512) {
        throw new Error(`SHA-512 mismatch: ${dependency.filename}`);
    }
    if (!fs.existsSync(jar)) fs.writeFileSync(jar, bytes);

    const profile = path.join(base, withLuckPerms ? 'luckperms' : 'vanilla');
    const mods = path.join(profile, 'mods');
    fs.mkdirSync(mods, {recursive: true});
    const eula = path.join(profile, 'eula.txt');
    if (!fs.existsSync(eula)) fs.writeFileSync(eula, 'eula=false\n');
    const properties = path.join(profile, 'server.properties');
    if (!fs.existsSync(properties)) {
        fs.writeFileSync(properties, 'server-ip=127.0.0.1\nserver-port=25578\nonline-mode=false\nview-distance=2\nsimulation-distance=2\nlevel-type=minecraft:flat\nspawn-protection=0\nmax-players=2\nenable-status=false\n');
    }
    if (!/^eula=true\s*$/m.test(fs.readFileSync(eula, 'utf8'))) {
        throw new Error(`Accept the Minecraft EULA in ${eula} before running the test.`);
    }
    const unexpected = fs.readdirSync(mods).filter(name => /luckperms.*\.jar$/i.test(name) && (!withLuckPerms || name !== dependency.filename));
    if (unexpected.length) throw new Error(`Unexpected LuckPerms JARs in ${mods}: ${unexpected.join(', ')}`);
    if (withLuckPerms) fs.copyFileSync(jar, path.join(mods, dependency.filename));
    return profile;
}

module.exports = {prepare};

