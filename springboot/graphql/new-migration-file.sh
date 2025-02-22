#!/usr/bin/env bash

# To create new flyway database migration script file in "src/main/resources/migration"
# The command template is:
#       bash ./new-migration-file.sh "<comments>"
#
# e.g.  bash ./new-migration-file.sh "upgrade camunda from 7.15 to 7.16"
#       then the "src/main/resources/migration/V20211110_1201__upgrade_camunda_from_7.15_to_7.16.sql" file
#       should be created

set -e

work_dir=$(cd $(dirname "$0") || exit; pwd)

if [[ -n $1 ]];then
	_cmd="${work_dir}/src/main/resources/migration/V`date +%Y%m%d_%H%M`__${1// /_}.sql"
	touch "$_cmd"
	echo "File $_cmd is created"
else
	echo 'Please press migration comments.'
fi
