# This script will run on a Jenkins Linux slave and resynchronizes the NTP offset.

cat <<EOF > remote.sh
# Set the PATH and TERM because piping a script to sshpass via STDIN does not initialize a terminal
export PATH=$PATH:/usr/local/sbin:/usr/sbin:/sbin
export TERM=xterm
 
sudo cat /etc/resolv.conf

while read line; do
  ip="$(grep -oE '[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}' <<< "$line")"
  echo "$ip"
done < "resolv.conf"

sudo service ntpd stop
sudo ntpd $ip
sudo service ntpd start 
 
EOF
 
# Execute the script on the remote Linux machine
sshpass -p "$__password__" ssh -o StrictHostKeyChecking=no $__username__@$_address_ < remote.sh
