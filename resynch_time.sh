# This script will run on a Jenkins Linux slave and resynchronizes the NTP offset.

# Set the PATH and TERM because piping a script to sshpass via STDIN does not initialize a terminal

sudo cat /etc/resolv.conf > resolv.txt
cat resolv.txt
sudo service ntpd restart
echo "the ip is $ip denote it"
initctl list |grep -i tty
