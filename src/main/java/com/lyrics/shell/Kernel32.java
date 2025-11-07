package com.lyrics.shell;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {
    Kernel32 INSTANCE = (Kernel32) Native.load("kernel32", Kernel32.class);

    boolean Beep(int dwFreq, int dwDuration);
}
