exception Fin;;
exception Connected;;

open OcsfmlAudio


(******* IHM *******)
let _ = GMain.init()

let window = GWindow.window
  ~title:"Jam Session"
  ~height:400
  ~width:400
  ~border_width:5 ()

let vbox = GPack.vbox
  ~packing:window#add ()

let hbox = GPack.hbox
  ~packing:vbox#add ()

let scrollChat = GBin.scrolled_window
  ~packing:hbox#add ()
  
let textviewChat = GText.view ~packing:scrollChat#add_with_viewport ()

let scrollUser = GBin.scrolled_window
  ~packing:hbox#add ()
  
let textviewUser = GText.view ~packing:scrollUser#add_with_viewport ()

let entry = GEdit.entry
  ~text:""
  ~packing:(vbox#pack ~fill:false) ()

(***** lit un texte sur l'entrée ****)
let my_input_line  fd = 
  let s = " "  and  r = ref "" 
  in while (ThreadUnix.read fd s 0 1 > 0) && s.[0] <> '\n' do r := !r ^s done ;
  !r ;;






(***** met à jour la liste des utilisateurs ****)
let majUsers l =
  let rec majUsersRec l acc = 
    match l with
    |a::b -> majUsersRec b acc^a^"\n"
    |_ -> acc
  in majUsersRec l "";;




class virtual client serv p c =
object(s)
  val sock = ThreadUnix.socket Unix.PF_INET Unix.SOCK_STREAM 0;
  val port_num = p;
  val server = serv;
  val user = c;
  method start() =
    let host = Unix.gethostbyname server in
    let h_addr = host.Unix.h_addr_list.(0) in
    let sock_addr = Unix.ADDR_INET(h_addr,port_num) in
    Unix.connect sock sock_addr;
    s#connect sock sock_addr;
    Unix.close sock
      
  method virtual connect : Unix.file_descr -> Unix.sockaddr ->unit

end;;

class client_maj s p c =
object(this)
  inherit client s p c
  method connect s sa =
    window#connect#destroy ~callback:GMain.Main.quit;
    entry#connect#activate ~callback:(fun() -> this#send (s,sa));
    let si = "CONNECT/"^c^"\n" in
    ignore (ThreadUnix.write s si 0 (String.length si));
    
    let t1 = Thread.create this#receive (s,sa) in
    window#show();
    GMain.Main.main ();
    Thread.join t1;
    
  method receive arg =
    let (s,sa) = arg in
    let hist = ref "" in
    try
      while true do
	let so = (my_input_line s) in
	let l = (Str.split (Str.regexp "/") so) in
	match (List.hd l) with
	|"WELCOME" -> 
	  begin
	    hist := !hist^"\n"^so;
	    textviewChat#buffer#set_text !hist;
	    Printf.printf "%s\n" so; 
	    flush stdout;
	    let so = (my_input_line s) in
	    let l = (Str.split (Str.regexp "/") so) in
	    match (List.hd l) with
	    |"AUDIO_PORT" -> 
	      let port = (List.hd (List.tl l)) in
	      let sock = ThreadUnix.socket Unix.PF_INET Unix.SOCK_STREAM 0 in
	      let host = Unix.gethostbyname server in
	      let h_addr = host.Unix.h_addr_list.(0) in
	      let sock_addr = Unix.ADDR_INET(h_addr,(int_of_string port)) in
	      Unix.connect sock sock_addr;
	   
	    |_->raise Fin
	  end

	|"EXITED"->
	  hist := !hist^"\n"^so;
	  textviewChat#buffer#set_text !hist;
	|"LISTEN"->
	  hist := !hist^"\n"^so;
	  textviewChat#buffer#set_text !hist;
	|"ERROR"->
	  hist := !hist^"\n"^so;
	  textviewChat#buffer#set_text !hist;
	|"AUDIO_OK"-> 
	  hist := !hist^"\n"^so;
	  textviewChat#buffer#set_text !hist;
	|"ACK_OPTS"->
	  hist := !hist^"\n"^so;
	  textviewChat#buffer#set_text !hist;
	|"LIST" ->textviewUser#buffer#set_text  (majUsers (List.tl l));
	|_->
	  hist := !hist^"\n"^so;
	  textviewChat#buffer#set_text !hist;
      done
    with Fin -> Unix.close sock

  method receiveAudio arg =  
    let (s,sa) = arg in
    let buffer = new buffer in
    let sound = new sound in
    while true do
      begin
	ignore (ThreadUnix.read s buffer 0 1) ;
	sound#set_buffer buffer;
	sound#play;
	while(sound#get_status <> Stopped) do
	done
      end
    done

  method sendAudio arg =
    let (s,sa) = arg in
    let buffer = 

  method send arg =
    let (s,sa) = arg in
    (* while true do
       let si = (my_input_line Unix.stdin)^"\n" in *)
    let si = entry#text^"\n" in
    ignore (ThreadUnix.write s si 0 (String.length si));
    entry#set_text "";
    let l = (Str.split (Str.regexp "/") si) in
    match (List.hd l) with
    |"EXIT"->Thread.exit()
    |_-> ()
(*done*)
      
(* | Connected -> this#treat s sa*)
      
end;;



let main() =
  if Array.length Sys.argv < 3
  then Printf.printf "usage : client server port\n"
  else 

    let port = int_of_string(Sys.argv.(2))
    and s = (Sys.argv.(1)) 
    and c = (Sys.argv.(3)) in
    (new client_maj s port c)#start();;

main();;
