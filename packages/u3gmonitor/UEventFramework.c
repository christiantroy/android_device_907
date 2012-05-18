/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <poll.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <linux/netlink.h>
#include <cutils/log.h>

#include "UEventFramework.h"

static int fd = -1;

/* Returns 0 on failure, 1 on success */
int uevent_init()
{
    struct sockaddr_nl addr;
    int sz = 64*1024;
    int s;

    memset(&addr, 0, sizeof(addr));
    addr.nl_family = AF_NETLINK;
    addr.nl_pid = getpid();
    addr.nl_groups = 0xffffffff;

    s = socket(PF_NETLINK, SOCK_DGRAM, NETLINK_KOBJECT_UEVENT);
    if(s < 0)
        return 0;

    setsockopt(s, SOL_SOCKET, SO_RCVBUFFORCE, &sz, sizeof(sz));

    if(bind(s, (struct sockaddr *) &addr, sizeof(addr)) < 0) {
        close(s);
        return 0;
    }

    fd = s;
    return (fd > 0);
}

static void parse_event(const char *msg, struct uevent *uevent)
{
	uevent->action = "";
    uevent->path = "";
    uevent->subsystem = "";
    uevent->firmware = "";
    uevent->major = -1;
    uevent->minor = -1;
    uevent->partition_name = NULL;
    uevent->partition_num = -1;
	
    /* currently ignoring SEQNUM */
    while(*msg) {
//    	SLOGI("%s,%d,msg=%s\n",__FUNCTION__,__LINE__,msg);
    	
        if(!strncmp(msg, "ACTION=", 7)) {
            msg += 7;
            uevent->action = msg;
        } else if(!strncmp(msg, "DEVPATH=", 8)) {
            msg += 8;
            uevent->path = msg;
        } else if(!strncmp(msg, "SUBSYSTEM=", 10)) {
            msg += 10;
            uevent->subsystem = msg;
        } else if(!strncmp(msg, "DEVTYPE=", 8)) {
            msg += 8;
            uevent->devtype = msg;
        }else if(!strncmp(msg, "FIRMWARE=", 9)) {
            msg += 9;
            uevent->firmware = msg;
        } else if(!strncmp(msg, "MAJOR=", 6)) {
            msg += 6;
            uevent->major = atoi(msg);
        } else if(!strncmp(msg, "MINOR=", 6)) {
            msg += 6;
            uevent->minor = atoi(msg);
        } else if(!strncmp(msg, "PARTN=", 6)) {
            msg += 6;
            uevent->partition_num = atoi(msg);
        } else if(!strncmp(msg, "PARTNAME=", 9)) {
            msg += 9;
            uevent->partition_name = msg;
        }

        /* advance to after the next \0 */
        while(*msg++)
            ;
    }   
}

#define UEVENT_MSG_LEN  1024

int uevent_next_event(uevent_cb cb)
{
    struct pollfd fds;   
    struct uevent event;    
    char buffer[UEVENT_MSG_LEN+2];
    int count,nr;
           
    while (1) {        
        fds.fd = fd;
    	fds.events = POLLIN;
    	fds.revents = 0;
    	
        nr = poll(&fds, 1, -1);
     	     	
        if(nr > 0 && fds.revents == POLLIN) 
        {
            memset(buffer, 0, UEVENT_MSG_LEN+2);
            count = recv(fd, buffer, UEVENT_MSG_LEN+2, 0);
            if (count > 0) {
               parse_event(buffer, &event); 
               (*cb)(&event);
            } 
        }
    }
        
    // won't get here
    return 0;
}



