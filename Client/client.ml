exception Fin;;
exception Connected;;

let my_input_line  fd = 
  let s = " "  and  r = ref "" 
  in while (ThreadUnix.read fd s 0 1 > 0) && s.[0] <> '\n' do r := !r ^s done ;
  !r ;;

class virtual client serv p =
object(s)
  val sock = ThreadUnix.socket Unix.PF_INET Unix.SOCK_STREAM 0;
  val port_num = p;
  val server = serv;

  method start() =
    let host = Unix.gethostbyname server in
    let h_addr = host.Unix.h_addr_list.(0) in
    let sock_addr = Unix.ADDR_INET(h_addr,port_num) in
    Unix.connect sock sock_addr;
    s#connect sock sock_addr;
    Unix.close sock
      
  method virtual connect : Unix.file_descr -> Unix.sockaddr ->unit

end;;

class client_maj s p =
object(this)
  inherit client s p
  method connect s sa =
    try 
      let window = GWindow.window 
	~title:"IHM"
	~width:200
	~height:100
	~border_width:10 () in
      window#connect#destroy ~callback:GMain.Main.quit;
      let vbox = GPack.vbox ~packing:window#add() in
      let entry = GEdit.entry ~max_length:500 ~packing:vbox#add() in
      window#show();
      GMain.Main.main();
      while true do
	let si = (my_input_line Unix.stdin)^"\n" in
	ignore (ThreadUnix.write s si 0 (String.length si));
	let so = (my_input_line s) in
	let l = (Str.split (Str.regexp "/") so) in
	match (List.hd l) with 
	|"ERROR" -> begin Printf.printf "%s\n" so; flush stdout end
	|"EXIT" -> raise Fin
	|"WELCOME" -> 
	  begin
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
	      let so = (my_input_line s) in
	      Printf.printf "%s\n" so; 
	      flush stdout;
	      let so = (my_input_line s) in
	      Printf.printf "%s\n" so; 
	      flush stdout;  
	      let t1 = Thread.create this#receive (s,sa) in
	      let t2 = Thread.create this#send (s,sa) in
	      Thread.join t1;
	      Thread.join t2;
	      Unix.close sock;
	      raise Fin
	    |_->raise Fin
	  end
	|_ -> raise Fin
	done
    with Fin -> ()

  method receive arg =
    let (s,sa) = arg in
    while true do
      let so = (my_input_line s) in
      let l = (Str.split (Str.regexp "/") so) in
      match (List.hd l) with
      |"EXITED"-> 
	begin
	  Printf.printf "%s\n" so; 
	  flush stdout;
	end
      |"LISTEN"->
	begin
	  Printf.printf "%s\n" so; 
	  flush stdout;
	end
      |_->	
	begin
	  Printf.printf "%s\n" so; 
	  flush stdout;
	end
    done

  method send arg =
    let (s,sa) = arg in
    while true do
      let si = (my_input_line Unix.stdin)^"\n" in
      ignore (ThreadUnix.write s si 0 (String.length si));
      let l = (Str.split (Str.regexp "/") si) in
      match (List.hd l) with
      |"EXIT"->Thread.exit()
      |_->()
    done

(* | Connected -> this#treat s sa*)
      
end;;

let main() =
  if Array.length Sys.argv < 3
  then Printf.printf "usage : client server port\n"
  else 
    let port = int_of_string(Sys.argv.(2))
    and s = (Sys.argv.(1)) in
    (new client_maj s port)#start();;

main();;
