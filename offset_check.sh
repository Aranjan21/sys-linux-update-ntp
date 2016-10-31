# This script will run on a Jenkins Linux slave and returns the NTP offset.

cat <<EOF > remote.sh
# Set the PATH and TERM because piping a script to sshpass via STDIN does not initialize a terminal
export PATH=$PATH:/usr/local/sbin:/usr/sbin:/sbin
export TERM=xterm
 
ntp_offset=\`ntpq -pn | /usr/bin/awk 'BEGIN { offset=1000 } \$1 ~ /\*/ { offset=\$9 } END { print offset }'\`
echo \$ntp_offset

EOF
 
# Execute the script on the remote Linux machine
sshpass -p "$__password__" ssh -o StrictHostKeyChecking=no $__username__@$_address_ < remote.sh