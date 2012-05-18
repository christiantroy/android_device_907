# Copyright (C) 2012 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# /system/bin
PRODUCT_COPY_FILES += \
	device/softwinner/907/prebuilt/bin/fsck.exfat:system/bin/fsck.exfat \
	device/softwinner/907/prebuilt/bin/mkfs.exfat:system/bin/mkfs.exfat \
	device/softwinner/907/prebuilt/bin/mount.exfat:system/bin/mount.exfat \
	device/softwinner/907/prebuilt/bin/ntfs-3g:system/bin/ntfs-3g \
	device/softwinner/907/prebuilt/bin/ntfs-3g.probe:system/bin/ntfs-3g.probe \
	device/softwinner/907/prebuilt/bin/mkntfs:system/bin/mkntfs \
	device/softwinner/907/prebuilt/bin/reboot-recovery.sh:system/bin/reboot-recovery.sh \
	device/softwinner/907/prebuilt/bin/usb_modeswitch:system/bin/usb_modeswitch \
	device/softwinner/907/prebuilt/bin/virtuous_oc:system/bin/virtuous_oc \
	device/softwinner/907/prebuilt/bin/wpa_cli:system/bin/wpa_cli \
	device/softwinner/907/prebuilt/bin/wpa_supplicant:system/bin/wpa_supplicant \
	device/softwinner/907/prebuilt/bin/zipalign:system/bin/zipalign

# /system/etc
PRODUCT_COPY_FILES += \
	device/softwinner/907/prebuilt/etc/ppp/ip-down:system/etc/ppp/ip-down \
	device/softwinner/907/prebuilt/etc/ppp/ip-up:system/etc/ppp/ip-up \
	device/softwinner/907/prebuilt/etc/ppp/call-pppd:system/etc/ppp/call-pppd \
	device/softwinner/907/prebuilt/etc/wifi/wpa_supplicant.conf:system/etc/wifi/wpa_supplicant.conf \
	device/softwinner/907/prebuilt/etc/firmware/ath3k-1.fw:system/etc/firmware/ath3k-1.fw \
	device/softwinner/907/prebuilt/etc/init.d/01modules:system/etc/init.d/01modules \
	device/softwinner/907/prebuilt/etc/init.d/02kernel:system/etc/init.d/02kernel \
	device/softwinner/907/prebuilt/etc/init.d/04mount:system/etc/init.d/04mount \
	device/softwinner/907/prebuilt/etc/init.d/70zipalign:system/etc/init.d/70zipalign \
	device/softwinner/907/prebuilt/etc/init.d/89virtuous_oc:system/etc/init.d/89virtuous_oc \
	device/softwinner/907/prebuilt/etc/3g_dongle.cfg:system/etc/3g_dongle.cfg \
	device/softwinner/907/prebuilt/etc/camera.cfg:system/etc/camera.cfg \
	device/softwinner/907/prebuilt/etc/gps.conf:system/etc/gps.conf \
	device/softwinner/907/prebuilt/etc/media_profiles.xml:system/etc/media_profiles.xml \
	device/softwinner/907/prebuilt/etc/usb_modeswitch.sh:system/etc/usb_modeswitch.sh \
	device/softwinner/907/prebuilt/etc/vold.fstab:system/etc/vold.fstab

PRODUCT_COPY_FILES += \
	$(call find-copy-subdir-files,*,device/softwinner/907/prebuilt/etc/usb_modeswitch.d,system/etc/usb_modeswitch.d)

PRODUCT_COPY_FILES += \
	$(call find-copy-subdir-files,*,device/softwinner/907/prebuilt/etc/virtuous_oc,system/etc/virtuous_oc)

# /system/lib
PRODUCT_COPY_FILES += \
	device/softwinner/907/prebuilt/lib/egl/libEGL_mali.so:system/lib/egl/libEGL_mali.so \
	device/softwinner/907/prebuilt/lib/egl/libGLESv1_CM_mali.so:system/lib/egl/libGLESv1_CM_mali.so \
	device/softwinner/907/prebuilt/lib/egl/libGLESv2_mali.so:system/lib/egl/libGLESv2_mali.so \
	device/softwinner/907/prebuilt/lib/hw/camera.exDroid.so:system/lib/hw/camera.exDroid.so \
	device/softwinner/907/prebuilt/lib/hw/display.sun4i.so:system/lib/hw/display.sun4i.so \
	device/softwinner/907/prebuilt/lib/hw/gralloc.sun4i.so:system/lib/hw/gralloc.sun4i.so \
	device/softwinner/907/prebuilt/lib/hw/sensors.exDroid.so:system/lib/hw/sensors.exDroid.so \
	device/softwinner/907/prebuilt/lib/liballwinner-ril.so:system/lib/liballwinner-ril.so \
	device/softwinner/907/prebuilt/lib/libMali.so:system/lib/libMali.so \
	device/softwinner/907/prebuilt/lib/libUMP.so:system/lib/libUMP.so

# /system/usr
PRODUCT_COPY_FILES += \
	device/softwinner/907/prebuilt/usr/idc/ft5x_ts.idc:system/usr/idc/ft5x_ts.idc \
	device/softwinner/907/prebuilt/usr/idc/Goodix-TS-board-3.idc:system/usr/idc/Goodix-TS-board-3.idc \
	device/softwinner/907/prebuilt/usr/keylayout/axp20-supplyer.kl:system/usr/keylayout/axp20-supplyer.kl \
	device/softwinner/907/prebuilt/usr/keylayout/sun4i-keyboard.kl:system/usr/keylayout/sun4i-keyboard.kl

# prebuilt kernel modules
PRODUCT_COPY_FILES += \
	$(call find-copy-subdir-files,*,device/softwinner/907/prebuilt/vendor/modules,system/vendor/modules)

PRODUCT_COPY_FILES += \
	$(call find-copy-subdir-files,*,device/softwinner/907/prebuilt/lib/modules,system/lib/modules)