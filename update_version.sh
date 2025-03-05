#!/bin/bash

POM_FILE="pom.xml"

# Function to increment version based on flag
increment_version() {
    local version="$1"
    local type="$2"

    # Extract version parts
    IFS='.' read -r major minor patch <<< "$version"

    case "$type" in
        major)
            ((major++))
            minor=0
            patch=0
            ;;
        minor)
            ((minor++))
            patch=0
            ;;
        patch|*)
            ((patch++))
            ;;
    esac

    echo "$major.$minor.$patch-SNAPSHOT"
}

# Extract the version below <artifactId>api-gateway</artifactId>
CURRENT_VERSION=$(awk '
    /<artifactId>api-gateway<\/artifactId>/ { found=1 }
    found && /<version>/ {
        gsub(/<version>|<\/version>/, "", $0)
        gsub(/^[ \t]+|[ \t]+$/, "", $0)  # Trim spaces
        print $0
        exit
    }
' "$POM_FILE")

# Check if version is found
if [[ -z "$CURRENT_VERSION" ]]; then
    echo "Error: Unable to find <version> tag for <artifactId>api-gateway</artifactId> in pom.xml"
    exit 1
fi

# Set default increment type (patch) or use provided flag
INCREMENT_TYPE="patch"  # Default
if [[ $# -gt 0 ]]; then
    case "$1" in
        major|minor|patch) INCREMENT_TYPE="$1" ;;
        *) echo "Invalid argument. Use: major, minor, or patch"; exit 1 ;;
    esac
fi

# Get the new version with -SNAPSHOT
NEW_VERSION=$(increment_version "$CURRENT_VERSION" "$INCREMENT_TYPE")

# Update only the version below <artifactId>api-gateway</artifactId>
awk -v new_version="$NEW_VERSION" '
    /<artifactId>api-gateway<\/artifactId>/ { found=1 }
    found && /<version>/ && !updated {
        sub(/<version>[ \t]*[^<]+[ \t]*<\/version>/, "<version>" new_version "</version>")
        updated=1
    }
    { print }
' "$POM_FILE" > pom.xml.tmp && mv pom.xml.tmp "$POM_FILE"

echo "Updated version: $CURRENT_VERSION -> $NEW_VERSION"