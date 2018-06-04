# This script will run on a Jenkins Linux slave and resynchronizes the NTP offset.

cat <<EOF > remote.sh
# Set the PATH and TERM because piping a script to sshpass via STDIN does not initialize a terminal
export PATH=$PATH:/usr/local/sbin:/usr/sbin:/sbin
export TERM=xterm
 
sudo cat /etc/resolv.conf > resolv.txt

cat resolv.txt

sudo service ntpd restartS

EOF
echo "the ip is $ip denote it"
