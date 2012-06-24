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
#include <stdlib.h>
#include <ctype.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <fcntl.h>
#include <dirent.h>

#define LOG_TAG "DevListener"

#include "cutils/log.h"

#include "NetlinkManager.h"

int main() {
    pid_t pid, sid;
    
    pid = fork();
    if (pid < 0)
      exit(EXIT_FAILURE);
    
    if (pid > 0)
      exit(EXIT_SUCCESS);
    
    umask(0);
    sid = setsid();
    if (sid < 0)
      exit(EXIT_FAILURE);
    
    if ((chdir("/")) < 0)
      exit(EXIT_FAILURE);
    
    close(STDIN_FILENO);
    close(STDOUT_FILENO);
    close(STDERR_FILENO);
    
    NetlinkManager *nm;

    if (!(nm = NetlinkManager::Instance())) {
        SLOGE("Unable to create NetlinkManager");
        exit(EXIT_FAILURE);
    };

    if (nm->start()) {
        SLOGE("Unable to start NetlinkManager (%s)", strerror(errno));
        exit(EXIT_FAILURE);
    }

    // Eventually we'll become the monitoring thread
    while(1) {
        sleep(1000);
    }

    SLOGI("DevListener exiting");
    exit(EXIT_SUCCESS);
}
