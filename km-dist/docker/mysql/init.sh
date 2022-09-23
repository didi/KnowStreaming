if [ "$MYSQL_DATABASE" ]; then
  "${mysql[@]}" < /docker-entrypoint-initdb.d/initsql
else
  echo "CREATE DATABASE IF NOT EXISTS ks ;" | "${mysql[@]}"
  "${mysql[@]}" ks < /docker-entrypoint-initdb.d/initsql
fi
