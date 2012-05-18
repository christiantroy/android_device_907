#!/system/bin/sh

echo -n boot-recovery | busybox dd of=/dev/block/nandf count=1 conv=sync; sync; reboot
