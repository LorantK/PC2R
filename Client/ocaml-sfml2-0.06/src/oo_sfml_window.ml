(** OO style interface for sfml-window *)
(* Copyright (C) 2012 Florent Monnier (email: monnier dot florent on gmail)
   Code provided under MIT license:
   See the file COPYING.txt or this URL:
   https://secure.wikimedia.org/wikipedia/en/wiki/MIT_License
   This software is provided "AS IS", without warranty of any kind. *)


class window
  ?style ?bpp ?depth ?stencil ?antialiasing ?version (width, height) title =
  object
    val this =
      SFWindow.make ?style ?bpp ?depth ?stencil ?antialiasing ?version
        (width, height) title

(* TODO: SFWindow.createFromHandle : handle:nativeint -> settings:window_settings -> t *)

    method is_open = SFWindow.isOpen this
    method close () = SFWindow.close this
    method display () = SFWindow.display this
    method destroy () = SFWindow.destroy this

    method poll_event () = SFWindow.pollEvent this
    method wait_event () = SFWindow.waitEvent this

    method size = SFWindow.getSize this
    method width = SFWindow.getWidth this
    method height = SFWindow.getHeight this

    method set_size ~size = SFWindow.setSize this ~size
    method set_size2 ~width ~height = SFWindow.setSize2 this ~width ~height

    method set_visible ~visible = SFWindow.setVisible this ~visible
    method set_key_repeat_enabled ~enabled = SFWindow.setKeyRepeatEnabled this ~enabled
    method set_active ~active = SFWindow.setActive this ~active
    method set_mouse_cursor_visible ~visible = SFWindow.setMouseCursorVisible this ~visible

    method set_vertical_sync_enabled ~enabled = SFWindow.setVerticalSyncEnabled this ~enabled
    method set_position ~pos = SFWindow.setPosition this ~pos
    method set_position2 ~left ~top = SFWindow.setPosition2 this ~left ~top
    method set_framerate_limit ~limit = SFWindow.setFramerateLimit this ~limit
    method set_joystick_threshold ~threshold = SFWindow.setJoystickThreshold this ~threshold
  end
