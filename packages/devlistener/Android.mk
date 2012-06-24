LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES:=                                      \
                  main.cpp                             \
                  NetlinkManager.cpp                   \
                  NetlinkHandler.cpp

LOCAL_MODULE:= devlistener

LOCAL_MODULE_TAGS := optional 

LOCAL_C_INCLUDES :=                          \
                    $(KERNEL_HEADERS)        \
                    external/openssl/include

LOCAL_CFLAGS := 

LOCAL_SHARED_LIBRARIES :=               \
                          libsysutils   \
                          libcutils     \
                          libcrypto

include $(BUILD_EXECUTABLE)

