#!/bin/sh

#ocamlc -o client.exe -thread -custom unix.cma threads.cma str.cma client.ml -cclib -lthreads -cclib -lunix

#ocamlfind ocamlc -g -thread -package lablgtk2 -custom str.cma -cclib -lthreads -cclib -lunix -linkpkg client.ml -o client.exe


#ocamlfind ocamlc -linkpkg -package 'ocsfml.window' -package 'ocsfml.audio' -package 'ocsfml.graphics' testRecord.ml -o testRecord


#ocamlfind ocamlc -g -linkpkg 'lablgtk2' -thread -custom str.cma -cclib -lthreads -cclib -lunix client.ml -o client.exe



#ocamlfind ocamlc -g -thread -custom str.cma -cclib -lthreads -cclib -lunix -linkpkg -package 'lablgtk2' -package 'ocsfml.audio' client.ml -o client.exe



# 1 ocamlfind ocamlc -g  -linkpkg -package 'lablgtk2' -package 'ocsfml.audio' -o client.exe -thread -custom str.cma client.ml -cclib -lthreads -cclib -lunix 

ocamlfind ocamlopt -linkpkg -package 'lablgtk2' -package 'ocsfml.audio' -thread  str.cmxa bigarray.cmxa  client.ml -o client.exe -cclib -lthreads -cclib -lunix
