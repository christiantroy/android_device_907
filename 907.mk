#
# Copyright (C) 2011 The Android Open-Source Project
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
#

PRODUCT_COPY_FILES := \
	device/softwinner/907/kernel:kernel \
	device/softwinner/907/init.rc:root/init.rc \
	device/softwinner/907/initlogo.rle:root/initlogo.rle \
	device/softwinner/907/init.sun4i.rc:root/init.sun4i.rc \
	device/softwinner/907/init.sun4i.usb.rc:root/init.sun4i.usb.rc \
	device/softwinner/907/ueventd.sun4i.rc:root/ueventd.sun4i.rc

PRODUCT_CHARACTERISTICS := tablet

PRODUCT_TAGS += dalvik.gc.type-precise

PRODUCT_PROPERTY_OVERRIDES += \
	ro.opengles.version = 131072 \
	debug.egl.hw=1 \
	ro.display.switch=1 \
	ro.sf.lcd_density=160 \
	hwui.render_dirty_regions=false \
	wifi.interface = wlan0 \
	wifi.supplicant_scan_interval = 150 \
	persist.sys.strictmode.visual=0 \
	persist.sys.strictmode.disable=1 \
	persist.sys.usb.config=mass_storage,adb \
	dalvik.vm.verify-bytecode=false \
	dalvik.vm.dexopt-flags=v=n,o=v \
	dalvik.vm.execution-mode=int:jit \
	persist.sys.timezone=Europe/Rome \
	persist.sys.language=en \
	persist.sys.country=US \
	ro.com.google.locationfeatures=1 \
	dalvik.vm.lockprof.threshold=500 \
	ro.kernel.android.checkjni=0 \
	dalvik.vm.checkjni=false \
	dalvik.vm.dexopt-data-only=1 \
	ro.vold.umsdirtyratio=20 \
	persist.sys.use_dithering=0 \
	persist.sys.purgeable_assets=0 \
	windowsmgr.max_events_per_sec=240 \
	view.touch_slop=2 \
	view.minimum_fling_velocity=25 \
	updateme.disableinstalledapps=1 \
	updateme.disablescripts=1 \
	ro.additionalmounts=/storage/sdcard1 \
	ro.vold.switchablepair=/storage/sdcard0,/storage/sdcard1 \
	persist.sys.vold.switchexternal=0 \
	ro.disable_phablet_ui=1

DEVICE_PACKAGE_OVERLAYS := device/softwinner/907/overlay

# Permissions
PRODUCT_COPY_FILES += \
	frameworks/native/data/etc/tablet_core_hardware.xml:system/etc/permissions/tablet_core_hardware.xml \
	frameworks/native/data/etc/android.hardware.camera.flash-autofocus.xml:system/etc/permissions/android.hardware.camera.flash-autofocus.xml \
	frameworks/native/data/etc/android.hardware.camera.front.xml:system/etc/permissions/android.hardware.camera.front.xml \
	frameworks/native/data/etc/android.hardware.telephony.gsm.xml:system/etc/permissions/android.hardware.telephony.gsm.xml \
	frameworks/native/data/etc/android.hardware.location.gps.xml:system/etc/permissions/android.hardware.location.gps.xml \
	frameworks/native/data/etc/android.hardware.wifi.xml:system/etc/permissions/android.hardware.wifi.xml \
	frameworks/native/data/etc/android.hardware.sensor.accelerometer.xml:system/etc/permissions/android.hardware.sensor.accelerometer.xml \
	frameworks/native/data/etc/android.hardware.sensor.light.xml:system/etc/permissions/android.hardware.sensor.light.xml \
	frameworks/native/data/etc/android.hardware.sensor.proximity.xml:system/etc/permissions/android.hardware.sensor.proximity.xml \
	frameworks/native/data/etc/android.hardware.touchscreen.multitouch.jazzhand.xml:system/etc/permissions/android.hardware.touchscreen.multitouch.jazzhand.xml \
	frameworks/native/data/etc/android.hardware.usb.host.xml:system/etc/permissions/android.hardware.usb.host.xml \
	frameworks/native/data/etc/android.hardware.usb.accessory.xml:system/etc/permissions/android.hardware.usb.accessory.xml \
	packages/wallpapers/LivePicker/android.software.live_wallpaper.xml:/system/etc/permissions/android.software.live_wallpaper.xml

# Publish that we support the live wallpaper feature.
PRODUCT_PACKAGES += \
	LiveWallpapers \
	LiveWallpapersPicker \
	MagicSmokeWallpapers \
	HoloSpiralWallpaper 

PRODUCT_PACKAGES += \
	VisualizationWallpapers \
	librs_jni

# Hardware libs
PRODUCT_PACKAGES += \
	gralloc.sun4i \
	hwcomposer.exDroid \
	lights.sun4i \
	display.sun4i \
        libcedarxbase \
        libcedarxosal \
	libcedarv \
	libcedarv_adapter \
        libcedarv_base \
	libCedarA \
	libCedarX \
	libstagefright_soft_cedar_h264dec \
        libswdrm \
	libaw_audio \
	libaw_audioa \
	libfacedetection \
	librtmp \
	libve \
	audio.primary.exDroid \
	audio.a2dp.default \
	audio.usb.default \
	libaudioutils \
	chat \
	u3gmonitor \
	devlistener

# CM9 apps
PRODUCT_PACKAGES += \
	com.android.future.usb.accessory

# EXT4 Support
PRODUCT_PACKAGES += \
	make_ext4fs \
	e2fsck

$(call inherit-product, build/target/product/full_base.mk)

# Should be after the full_base include, which loads languages_full
PRODUCT_AAPT_CONFIG := large xlarge mdpi
PRODUCT_AAPT_PREF_CONFIG := mdpi

PRODUCT_NAME := full_907
PRODUCT_DEVICE := 907

TARGET_SCREEN_HEIGHT := 768
TARGET_SCREEN_WIDTH := 1024