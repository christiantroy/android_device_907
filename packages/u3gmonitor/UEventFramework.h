#ifndef __UEVENT_FRAMEWORK_H__
#define __UEVENT_FRAMEWORK_H__

#if __cplusplus
extern "C" {
#endif 

struct uevent 
{
    const char *action;
    const char *path;
    const char *subsystem;
    const char *firmware;
    const char *partition_name;
    const char *devtype;
    int partition_num;
    int major;
    int minor;
};

typedef void (*uevent_cb)(struct uevent *event);

int uevent_init();
int uevent_next_event(uevent_cb cb);

#if __cplusplus
} // extern "C"
#endif 

#endif /*__UEVENT_FRAMEWORK_H__ */

