package com.lyrics.shell;

import com.sun.jna.*;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.StdCallLibrary.StdCallCallback;

public interface Shell32 extends StdCallLibrary {
    Shell32 INSTANCE = (Shell32) Native.load("shell32", Shell32.class);

    int ShellExecuteW(Pointer hwnd, String lpOperation, String lpFile, String lpParameters, String lpDirectory, int nShowCmd);
}

