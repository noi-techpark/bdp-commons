#!/bin/bash

echo
echo "!!! WE TEST ALL DATA COLLECTORS THAT HAVE BEEN CHANGED SINCE LAST COMMIT !!!"
echo
if [[ "${BRANCH_NAME}" == "development" ]]; then
    REF_BRANCH="origin/master"
    git fetch -- "$GIT_URL" +refs/heads/master:refs/remotes/origin/master
elif [[ "${BRANCH_NAME}" == "master" ]]; then
    REF_BRANCH="$GIT_PREVIOUS_SUCCESSFUL_COMMIT"
else
    REF_BRANCH="origin/development"
    git fetch -- "$GIT_URL" +refs/heads/development:refs/remotes/origin/development
fi

echo "Using REF_BRANCH = $REF_BRANCH"

ROOT_COMMIT=$(git cherry $REF_BRANCH|head -n1|awk '{print $2}')
DELTA_FILES=$(git diff --name-only HEAD "$ROOT_COMMIT" | grep data-collectors/ || true)
echo "Changed Data Collectors:"
for RECORD in ${DELTA_FILES[@]}; do
    echo "--> Checking delta for $RECORD"
    RECORD=$(echo "$RECORD" | sed -E 's#data-collectors/([^/]*)/.*$#\1#g')
    DC_SET+=("$RECORD")
done
DC_SET=($(printf "%s\n" "${DC_SET[@]}" | sort -u))
for DC_NAME in "${DC_SET[@]}"; do
    (cd "data-collectors/$DC_NAME" && mvn -B -U clean compile test)
done
echo
echo "!!! Ready !!!"
echo
