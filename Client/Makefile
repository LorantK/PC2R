
cible: client.exe

client.exe : 
	ocamlc -o $@ -thread -custom unix.cma threads.cma str.cma client.ml -cclib -lthreads -cclib -lunix
