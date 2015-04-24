open OcsfmlWindow
open OcsfmlGraphics
open OcsfmlAudio

let _ = begin
    let window = new render_window (VideoMode.create ~w:800 ~h:600 ()) "Ocsfml Window" in
  
    if SoundRecorder.is_available () then
      begin
	let recorder = new sound_buffer_recorder in
	recorder#start();
	
    (* event loop *)
    let rec event_loop () =
        match window#poll_event with
        | Some e -> begin 
          if e = Event.Closed then
	    begin
	      recorder#stop;
	      let buffer = recorder#get_buffer in
	      buffer#save_to_file "test.ogg";
	      window#close ;
	    end;
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
end
