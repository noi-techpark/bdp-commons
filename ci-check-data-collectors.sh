#!/bin/bash

set -e

# Perform a full check on all data collectors for our main branches, development and master
# All other branches (incl. PRs) we just go back to find the point in history where we branched
# out, and see which folders have changed files, and then execute the test only on those 
# data collectors.
if [[ "${BRANCH_NAME}" == "development" || "${BRANCH_NAME}" == "master" ]]; then
    echo
    echo "!!! WE TEST ALL DATA COLLECTORS !!!"
    echo
    DC_SET=($(find data-collectors/ -mindepth 1 -maxdepth 1 -type d -not -name '.*' | sed -E 's#data-collectors/(.*)$#\1#g' | sort))
else
    echo
    echo "!!! WE TEST ALL DATA COLLECTORS THAT HAVE BEEN CHANGED SINCE LAST COMMIT !!!"
    echo
    git fetch -- "$GIT_URL" +refs/heads/development:refs/remotes/origin/development
    echo "Using REF_BRANCH = origin/development"
    ROOT_COMMIT=$(git cherry origin/development|head -n1|awk '{print $2}')
    DELTA_FILES=$(git diff --name-only HEAD "$ROOT_COMMIT" | grep data-collectors/ || true)
    echo "Changed Data Collectors:"
    for RECORD in ${DELTA_FILES[@]}; do
        echo "--> Checking delta for $RECORD"
        RECORD=$(echo "$RECORD" | sed -E 's#data-collectors/([^/]*)/.*$#\1#g')
        DC_SET+=("$RECORD")
    done
    DC_SET=($(printf "%s\n" "${DC_SET[@]}" | sort -u))
fi


for DC_NAME in "${DC_SET[@]}"; do
    cd "data-collectors/$DC_NAME"
    if test -n "$DRYRUN"; then
        echo "DRYRUN: data-collectors/$DC_NAME"
    else
        echo
        echo "!!! CHECKING $DC_NAME !!!"
        echo
        mvn -B -U clean compile test
    fi
    cd -
done

echo
echo "!!! Ready !!!"
echo
