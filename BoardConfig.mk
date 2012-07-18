#
# Copyright (C) 2012 The Android Open-Source Project
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
TARGET_BOARD_PLATFORM := exDroid
TARGET_BOOTLOADER_BOARD_NAME := crane

USE_CAMERA_STUB := false
HAVE_HTC_AUDIO_DRIVER := true
BOARD_USES_GENERIC_AUDIO := true
BOARD_USES_GPS_TYPE := simulator

TARGET_NO_BOOTLOADER := true
TARGET_NO_RADIOIMAGE := true

#CPU stuff
TARGET_CPU_ABI := armeabi-v7a
TARGET_CPU_ABI2 := armeabi
TARGET_ARCH_VARIANT := armv7-a-neon
TARGET_GLOBAL_CFLAGS += -mtune=cortex-a8 -mfpu=neon -mfloat-abi=softfp
TARGET_GLOBAL_CPPFLAGS += -mtune=cortex-a8 -mfpu=neon -mfloat-abi=softfp
ARCH_ARM_HAVE_TLS_REGISTER := true

BOARD_HAVE_BLUETOOTH := true
BOARD_HAS_VIBRATOR_IMPLEMENTATION := ../../device/softwinner/907/vibrator.c

TARGET_USERIMAGES_USE_EXT4 := true
BOARD_BOOTIMAGE_PARTITION_SIZE := 33554432
BOARD_RECOVERYIMAGE_PARTITION_SIZE := 33554432
BOARD_SYSTEMIMAGE_PARTITION_SIZE := 268435456
BOARD_USERDATAIMAGE_PARTITION_SIZE := 1073741824
BOARD_FLASH_BLOCK_SIZE := 4096

#EGL stuff
BOARD_EGL_CFG := device/softwinner/907/egl.cfg
USE_OPENGL_RENDERER := true
BOARD_USE_SKIA_LCDTEXT := true
ENABLE_WEBGL := true

#Recovery Stuff
BOARD_CUSTOM_RECOVERY_KEYMAPPING := ../../device/softwinner/907/recovery_keys.c
BOARD_UMS_LUNFILE := "/sys/class/android_usb/android0/f_mass_storage/lun/file"
BOARD_UMS_2ND_LUNFILE := "/sys/class/android_usb/android0/f_mass_storage/lun1/file"
TARGET_RECOVERY_INITRC := device/softwinner/907/recovery_init.rc
BOARD_HAS_NO_SELECT_BUTTON := true

#Misc stuff
TARGET_USE_CUSTOM_LUN_FILE_PATH = "/sys/class/android_usb/android0/f_mass_storage/lun%d/file"
TARGET_USE_CUSTOM_SECOND_LUN_NUM := 1
TARGET_RECOVERY_PRE_COMMAND := "echo -n boot-recovery | busybox dd of=/dev/block/nandf count=1 conv=sync; sync"
BOARD_USE_LEGACY_TOUCHSCREEN := true
TARGET_HARDWARE_INCLUDE := $(TOP)/device/softwinner/907/libraries/include
TARGET_PROVIDES_INIT_RC := true
TARGET_BOOTANIMATION_PRELOAD := true
TARGET_RELEASETOOL_OTA_FROM_TARGET_SCRIPT := device/softwinner/907/releasetools/907_ota_from_target_files

# Wifi stuff
BOARD_WIFI_VENDOR := realtek
ifeq ($(BOARD_WIFI_VENDOR), realtek)
WPA_SUPPLICANT_VERSION := VER_0_8_X
BOARD_WPA_SUPPLICANT_DRIVER := NL80211
BOARD_WPA_SUPPLICANT_PRIVATE_LIB := lib_driver_cmd_rtl
BOARD_HOSTAPD_DRIVER        := NL80211
BOARD_HOSTAPD_PRIVATE_LIB   := lib_driver_cmd_rtl

BOARD_WLAN_DEVICE := rtl8192cu

WIFI_DRIVER_MODULE_NAME   := 8192cu
WIFI_DRIVER_MODULE_PATH   := "/system/lib/modules/8192cu.ko"

WIFI_DRIVER_MODULE_ARG    := ""
WIFI_FIRMWARE_LOADER      := ""
WIFI_DRIVER_FW_PATH_STA   := ""
WIFI_DRIVER_FW_PATH_AP    := ""
WIFI_DRIVER_FW_PATH_P2P   := ""
WIFI_DRIVER_FW_PATH_PARAM := ""

TARGET_CUSTOM_WIFI := ../../hardware/realtek/wlan/libhardware_legacy/wifi/wifi_realtek.c
endif

# Beware: set only prebuilt OR source+config
TARGET_PREBUILT_KERNEL := device/softwinner/907/kernel
BOARD_KERNEL_BASE := 0x40000000
BOARD_KERNEL_CMDLINE := console=ttyS0,115200 rw init=/init loglevel=8
