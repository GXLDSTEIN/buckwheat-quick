#!/usr/bin/env python3
"""Check that a release tag and APK signer match this app's update identity."""
import argparse
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parent.parent


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('tag')
    parser.add_argument('--certificate-report', type=Path)
    args = parser.parse_args()
    gradle = (ROOT / 'app/build.gradle.kts').read_text()
    version_name = re.search(r'versionName\s*=\s*"([^"]+)"', gradle).group(1)
    version_code = int(re.search(r'versionCode\s*=\s*(\d+)', gradle).group(1))
    if args.tag != f'v{version_name}' or version_code < 1:
        parser.error(f'tag must be v{version_name}; APK versionCode must be positive')
    if args.certificate_report:
        report = args.certificate_report.read_text()
        found = re.search(r'Signer #1 certificate SHA-256 digest:\s*([a-fA-F0-9]{64})', report)
        expected = (ROOT / '.github/release-certificate.sha256').read_text().strip()
        if not found or found.group(1).lower() != expected:
            parser.error('APK signing certificate differs from the installed Buckwheat Quick app')
    print(f'Release identity verified: {args.tag}, versionCode {version_code}')


if __name__ == '__main__':
    main()
