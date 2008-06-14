# this scripts loads gentoo and creates 
# a chrootable image that can be used to generate binary programs
# with different configurations
# Ideas taken from:
# http://kapcoweb.com/p/static/docs/jc-gentoo-howto/jc-gentoo-howto.html
# http://www.gentoo.org/proj/en/base/amd64/howtos/index.xml?part=1&chap=2


dir=gentoo32-O2

sudo mkdir /mnt/$dir
cd /mnt/$dir

sudo wget http://mirror.mcs.anl.gov/pub/gentoo/releases/x86/current/stages/stage3-x86-2007.0.tar.bz2
sudo tar -xvjpf stage3-x86-2007.0.tar.bz2
sudo rm stage3-x86-2007.0.tar.bz2
sudo cp -L /etc/resolv.conf /mnt/$dir/etc/
sudo cp -L /etc/passwd /mnt/$dir/etc/

echo "edit make.conf"

sudo emacs /mnt/$dir/etc/make.conf

sudo mount -o bind /dev /mnt/$dir/dev
sudo mount -o bind /dev/pts /mnt/$dir/dev/pts
sudo mount -o bind /dev/shm /mnt/$dir/dev/shm
sudo mount -o bind /proc /mnt/$dir/proc
sudo mount -o bind /proc/bus/usb /mnt/$dir/proc/bus/usb
sudo mount -o bind /sys /mnt/$dir/sys

echo "do the following to complete the installation:"
echo "sudo linux32 chroot /mnt/$dir/ /bin/bash"
echo "source /etc/profile"
echo "env-update"
echo "emerge sync"
# use equery to find files owned by a package.
echo "emerge gentoolkit"
echo "emerge glibc"