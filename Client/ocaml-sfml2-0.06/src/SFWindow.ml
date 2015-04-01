type t

type window_style =
  [ `titlebar
  | `resize  
  | `close   
  | `fullscreen
  | `default_style
  ]

type video_mode =
  { width : int;
    height : int;
    bitsPerPixel : int;
  }

type context_settings =
  { depthBits : int;          (** bits of the depth buffer *)
    stencilBits : int;        (** bits of the stencil buffer *)
    antialiasingLevel : int;  (** level of antialiasing *)
    majorVersion : int;       (** major number of the context version to create *)
    minorVersion : int;       (** minor number of the context version to create *)
  }

let mode ~width ~height ?(bpp = 32) () =
  { width = width;
    height = height;
    bitsPerPixel = bpp;
  }

let settings ?(depth = 0) ?(stencil = 0) ?(antialiasing = 0)
    ?(version = (2,0)) () =
  let majorVersion, minorVersion = version in
  { depthBits = depth;
    stencilBits = stencil;
    antialiasingLevel = antialiasing;
    majorVersion;
    minorVersion;
  }

external create: mode:video_mode -> title:string ->
  style:window_style list -> settings:context_settings -> t
  = "caml_sfWindow_create"

external createFromHandle: handle:nativeint -> settings:context_settings -> t
  = "caml_sfWindow_createFromHandle"


let make ?(style = [`titlebar; `resize; `close]) ?(bpp = 32)
    ?(depth = 0) ?(stencil = 8) ?(antialiasing = 0)
    ?(version = (2, 0))
    (width, height) title =
  let majorVersion, minorVersion = version in
  let mode =
    { width = width;
      height = height;
      bitsPerPixel = bpp;
    }
  and settings =
    { depthBits = depth;
      stencilBits = stencil;
      antialiasingLevel = antialiasing;
      majorVersion = majorVersion;
      minorVersion = minorVersion;
    }
  in
  (create ~mode ~title ~style ~settings)


external isOpen: t -> bool = "caml_sfWindow_isOpen"
external close: t -> unit = "caml_sfWindow_close"
external display: t -> unit = "caml_sfWindow_display"
external destroy: t -> unit = "caml_sfWindow_destroy"

external pollEvent: t -> SFEvent.t option = "caml_sfWindow_pollEvent"
external waitEvent: t -> SFEvent.t = "caml_sfWindow_waitEvent"

external getSize: t -> int * int = "caml_sfWindow_getSize"
external getWidth: t -> int = "caml_sfWindow_getWidth"
external getHeight: t -> int = "caml_sfWindow_getHeight"

(*
external getSettings: t -> context_settings = "caml_sfWindow_getSettings"
*)

external setSize: t -> size:int * int -> unit
  = "caml_sfWindow_setSize"

external setSize2: t -> width:int -> height:int -> unit
  = "caml_sfWindow_setSize2"

external setPosition: t -> pos:int * int -> unit
  = "caml_sfWindow_setPosition"

external setPosition2: t -> left:int -> top:int -> unit
  = "caml_sfWindow_setPosition2"

external getPosition: t -> int * int = "caml_sfWindow_getPosition"

external setTitle: t -> title:string -> unit = "caml_sfWindow_setTitle"

external setActive: t -> active:bool -> bool = "caml_sfWindow_setActive"

external setVisible: t -> visible:bool -> unit = "caml_sfWindow_setVisible"

external setMouseCursorVisible: t -> visible:bool -> unit
  = "caml_sfWindow_setMouseCursorVisible"

external setKeyRepeatEnabled: t -> enabled:bool -> unit
  = "caml_sfWindow_setKeyRepeatEnabled"

external setJoystickThreshold: t -> threshold:float -> unit
  = "caml_sfWindow_setJoystickThreshold"

external setFramerateLimit: t -> limit:int -> unit
  = "caml_sfWindow_setFramerateLimit"

external setVerticalSyncEnabled: t -> enabled:bool -> unit
  = "caml_sfWindow_setVerticalSyncEnabled"

external getMousePosition: relativeTo:t -> int * int = "caml_sfWindow_getMousePosition"
external setMousePosition: relativeTo:t -> int * int -> unit = "caml_sfWindow_setMousePosition"
(** these functions are also in [SFMouse] but without the [relativeTo] parameter *)
