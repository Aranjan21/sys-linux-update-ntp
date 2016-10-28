# This script will run on a Jenkins Linux slave. Its purpose is to restart a linux machine.

cat <<EOF > remote.sh
# Set the PATH and TERM because piping a script to sshpass via STDIN does not initialize a terminal
export PATH=$PATH:/usr/local/sbin:/usr/sbin:/sbin
export TERM=xterm
 
ntpq -pn | /usr/bin/awk 'BEGIN { offset=1000 } \$1 ~ /\*/ { offset=$9 } END { print offset }'

EOF
 
# Execute the script on the remote Linux machine
sshpass -p "$__password__" ssh -o StrictHostKeyChecking=no $__username__@$_address_ < remote.sh