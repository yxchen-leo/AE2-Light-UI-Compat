"""Verify a dependency inventory without writing to the supplied directory."""
import argparse
import hashlib
import json
from pathlib import Path


def sha256(path):
    with path.open('rb') as stream:
        return hashlib.file_digest(stream, 'sha256').hexdigest()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('manifest', type=Path)
    parser.add_argument('directory', type=Path)
    parser.add_argument('--strict', action='store_true', help='Also reject extra JAR/ZIP files')
    args = parser.parse_args()
    manifest = json.loads(args.manifest.read_text(encoding='utf-8'))
    root = args.directory.resolve(strict=True)
    entries = manifest['files']
    errors = []
    expected = set()
    for entry in entries:
        name = entry['file']
        path = (root / name).resolve()
        if not path.is_relative_to(root) or path == root:
            raise ValueError(f'Unsafe inventory path: {name}')
        if name in expected:
            raise ValueError(f'Duplicate inventory entry: {name}')
        expected.add(name)
        if not path.is_file():
            errors.append(f'MISSING {name}')
        elif path.stat().st_size != entry['size'] or sha256(path) != entry['sha256']:
            errors.append(f'MISMATCH {name}')
    if args.strict:
        actual = {p.relative_to(root).as_posix() for p in root.rglob('*')
                  if p.is_file() and p.suffix.lower() in {'.jar', '.zip'}}
        errors.extend(f'EXTRA {name}' for name in sorted(actual - expected))
    for error in errors:
        print(error)
    print(f'Checked {len(entries)} files; {len(errors)} problems. No files changed.')
    raise SystemExit(bool(errors))


if __name__ == '__main__':
    main()
