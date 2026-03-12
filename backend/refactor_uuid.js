const fs = require('fs');
const path = require('path');

function walkDir(dir, callback) {
    if (!fs.existsSync(dir)) return;
    fs.readdirSync(dir).forEach(f => {
        let dirPath = path.join(dir, f);
        let isDirectory = fs.statSync(dirPath).isDirectory();
        isDirectory ? walkDir(dirPath, callback) : callback(path.join(dir, f));
    });
}

function refactorUUID(dir) {
    walkDir(dir, function (filePath) {
        if (!filePath.endsWith('.java')) return;

        let content = fs.readFileSync(filePath, 'utf8');
        let originalContent = content;

        if (content.includes('UUID') || content.includes('Uuid')) {
            content = content.replace(/import org\.hibernate\.annotations\.UuidGenerator;\r?\n/g, '');

            // replace @UuidGenerator with @GeneratedValue
            if (content.includes('@UuidGenerator')) {
                if (!content.includes('import jakarta.persistence.GeneratedValue;')) {
                    content = content.replace(/(import jakarta\.persistence\.Id;\r?\n)/, '$1import jakarta.persistence.GeneratedValue;\nimport jakarta.persistence.GenerationType;\n');
                }
                content = content.replace(/@UuidGenerator\s+/g, '@GeneratedValue(strategy = GenerationType.IDENTITY)\n    ');
            }

            // Replaces exact UUID types, but watch out for variables named `uuid`
            // Let's replace type UUID with Long, EXCEPT java.util.UUID.randomUUID()
            content = content.replace(/\bUUID\s+id\b/g, 'Long id');
            content = content.replace(/\bUUID\b(?!\.randomUUID)/g, 'Long');
            // the above regex might have broken `java.util.UUID`.
            content = content.replace(/java\.util\.Long/g, 'java.util.UUID');
            content = content.replace(/import java\.util\.Long;\r?\n/g, ''); // remove it if it was incorrectly added

            if (content !== originalContent) {
                fs.writeFileSync(filePath, content, 'utf8');
                console.log('Modified: ' + filePath);
            }
        }
    });
}

refactorUUID(path.join(__dirname, 'src', 'main', 'java'));
refactorUUID(path.join(__dirname, 'src', 'test', 'java'));
