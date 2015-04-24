#!/bin/sh

#ocamlc -o client.exe -thread -custom unix.cma threads.cma str.cma client.ml -cclib -lthreads -cclib -lunix

#ocamlfind ocamlc -g -thread -package lablgtk2 -custom str.cma -cclib -lthreads -cclib -lunix -linkpkg client.ml -o client.exe


#ocamlfind ocamlc -linkpkg -package 'ocsfml.window' -package 'ocsfml.audio' -package 'ocsfml.graphics' testRecord.ml -o testRecord


#ocamlfind ocamlc -g -linkpkg 'lablgtk2' -thread -custom str.cma -cclib -lthreads -cclib -lunix client.ml -o client.exe

ocamlfind ocamlc -g -thread -linkpkg -package 'lablgtk2' -package 'ocsfml.audio'  -custom str.cma -cclib -lthreads -cclib -lunix client.ml -o client.exe


#ocamlfind ocamlc -g -thread -custom str.cma -cclib -lthreads -cclib -lunix -linkpkg -package 'lablgtk2' -package 'ocsfml.audio' client.ml -o client.exe
