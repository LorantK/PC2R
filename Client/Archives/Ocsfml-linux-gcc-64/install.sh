#!/bin/bash
ocamlfind install ocsfml META \
    System/ocsfmlSystem.cmi System/dllocsfmlsystem.so System/ocsfmlsystem.cma System/libocsfmlsystem.a System/ocsfmlsystem.cmxa System/ocsfmlsystem.a \
    Window/ocsfmlWindow.cmi Window/dllocsfmlwindow.so Window/ocsfmlwindow.cma Window/libocsfmlwindow.a Window/ocsfmlwindow.cmxa Window/ocsfmlwindow.a \
    Graphics/ocsfmlGraphics.cmi Graphics/dllocsfmlgraphics.so Graphics/ocsfmlgraphics.cma Graphics/libocsfmlgraphics.a Graphics/ocsfmlgraphics.cmxa Graphics/ocsfmlgraphics.a \
    Audio/ocsfmlAudio.cmi Audio/dllocsfmlaudio.so Audio/ocsfmlaudio.cma Audio/libocsfmlaudio.a Audio/ocsfmlaudio.cmxa Audio/ocsfmlaudio.a \
    Network/ocsfmlNetwork.cmi Network/dllocsfmlnetwork.so Network/ocsfmlnetwork.cma Network/libocsfmlnetwork.a Network/ocsfmlnetwork.cmxa Network/ocsfmlnetwork.a
