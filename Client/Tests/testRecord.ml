open OcsfmlWindow
open OcsfmlGraphics
open OcsfmlAudio

let _ = begin
    let window = new render_window (VideoMode.create ~w:800 ~h:600 ()) "Ocsfml Window" in



  
    let music = new music () in
    if not (music#open_from_file "keke.ogg")
    then raise OcsfmlSystem.LoadFailure ;

    music#play ;

    (* event loop *)
    let rec event_loop () =
        match window#poll_event with
        | Some e -> begin 
               if e = Event.Closed then window#close ;
               event_loop ()
          end
        | _ -> ()
    in

    let rec main_loop () =
        if window#is_open
        then begin
             event_loop () ;
             window#clear () ;
             window#display ;
             main_loop ()
        end
     in

     main_loop () ;   

end
