(deffacts age
	(person Rosa 30)
	(person Dulce 23)
	(person Barros 25)
	(person Tiago 32))
	
(deffacts surnames
	(fullname Rosa Araujo Pereira)
	(fullname Maria Sampaio Melo)
	(fullname Tiago Jose da Fonseca Silva)
	(fullname Dulce Barbosa))

(defrule rule1
	(person ?n ?i)
	=>
	(assert (name ?n))
	(assert (age ?i)))
	
/* (defrule rule2
	(person ?n ?i)
	=>
	(printout t "Person " ?n " has " ?i " years." crlf))*/
	
/*(defrule rule3
	(person ?n ?i)
	=>
	(printout t "Surname of " ?n " ?" crlf)
	(bind ?surname (read))
	(assert (nameandsurname ?n ?surname)))*/
	
/* (defrule rule31
	(nameandsurname ?n ?s)
	=>
	(printout t "Person " ?n " " ?s "." crlf)) */

/*(defrule rule4
	(fullname $? ?n)
	=>
	(printout t ?n crlf))*/
	
/*(defrule rule5
	(person ?n ?i)
	(fullname ?n $? ?s)
	=>
	(printout t "Person " ?n " " ?s " has " ?i " years." crlf))*/