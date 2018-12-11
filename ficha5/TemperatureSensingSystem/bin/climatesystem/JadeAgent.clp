(deftemplate ACLMessage 
              (slot communicative-act) (slot sender) (multislot receiver) 
              (slot reply-with) (slot in-reply-to) (slot envelope) 
              (slot conversation-id) (slot protocol) 
              (slot language) (slot ontology) (slot content) 
              (slot encoding) (multislot reply-to) (slot reply-by))

(deftemplate inMsgJess
	(slot sender)
	(slot temperature (type INTEGER))
)

(deftemplate outMsgJess
	(slot receiver)
	(slot action)
)

(deftemplate stats
	(slot name)
	(slot value (type INTEGER))
)

(deftemplate Department
	(slot name)
	(slot temperature (type INTEGER) (default 25))
	(slot machineWorking (type INTEGER) (default 0))
	(slot coolingTurnedOn (type INTEGER) (default 0))
	(slot heatingTurnedOn (type INTEGER) (default 0))
)

(defrule activateSystems
	?department <- (Department {((((temperature > 30) || (temperature < 20)) && (machineWorking == 0)) || ((temperature <= 30) && (temperature >= 20) && (machineWorking == 1)))})
	=>
	
	(if ( > ?department.temperature 30) then
		(changesystem cool ?department.name)
		(bind ?department.machineWorking 1)
		(bind ?department.coolingTurnedOn (++ ?department.coolingTurnedOn))
	elif ( < ?department.temperature 20) then
		(changesystem heat ?department.name)
		(bind ?department.machineWorking 1)
		(bind ?department.heatingTurnedOn (++ ?department.heatingTurnedOn))
	else
		(changesystem off ?department.name)
		(bind ?department.machineWorking 0)
	)
	;(printout t crlf crlf crlf "dep name: " ?department.name ", temp: " ?department.temperature ", machineWorking: " ?department.machineWorking ", #heat: " ?department.heatingTurnedOn ", #cool: " ?department.coolingTurnedOn  crlf crlf crlf)
)

(defrule updateTemperatures
	?msg <- (ACLMessage)
	?department <- (Department {(name == msg.sender)})
	=>
	(bind ?department.temperature (integer ?msg.content))
	(retract ?msg)
)


(defrule calculateStats
	?avg <- (accumulate
            (progn (bind ?sum 0)(bind ?count 0))
            (progn (bind ?sum (+ ?sum ?temperature))(++ ?count))
            (/ ?sum ?count)
            (Department (temperature ?temperature)))
            
   	?stddev <- (accumulate
            (progn (bind ?sum 0)(bind ?count -1))
            (progn (bind ?sum (sqrt (+ ?sum (* (- ?temperature ?avg) (- ?temperature ?avg))))) (++ ?count))
            (/ (round (* (/ ?sum ?count) 100)) 100)
            (Department (temperature ?temperature)))
    
=>
    ;(printout t "AVG: " ?*avg* ", STDDEV: " ?*stddev* crlf)
    (bind ?*avg* ?avg)
    (bind ?*stddev* ?stddev)
)

(defrule top3
	?*heat1* <- (accumulate
            (progn (bind ?maxCount 0)(bind ?finalNameDepartment ""))
            (progn (if (>= ?iteratedCount ?maxCount) then (bind ?finalNameDepartment ?iteratedNameDepartment)
            			(bind ?maxCount ?iteratedCount) ))
            (if (> ?maxCount 0) then (str-cat ?finalNameDepartment " (" ?maxCount ")") else "Undefined")
            (Department (heatingTurnedOn ?iteratedCount) (name ?iteratedNameDepartment)))
            
    	?*heat2* <- (accumulate
            (progn (bind ?maxCount 0)(bind ?finalNameDepartment ""))
            (progn (if (and (>= ?iteratedCount ?maxCount)(neq ?*heat1* (str-cat ?iteratedNameDepartment " (" ?iteratedCount ")"))) then (bind ?finalNameDepartment ?iteratedNameDepartment)
            			(bind ?maxCount ?iteratedCount) ))
            (if (> ?maxCount 0) then (str-cat ?finalNameDepartment " (" ?maxCount ")") else "Undefined")
            (Department (heatingTurnedOn ?iteratedCount) (name ?iteratedNameDepartment)))
    
    ?*heat3* <- (accumulate
            (progn (bind ?maxCount 0)(bind ?finalNameDepartment ""))
            (progn (if (and (>= ?iteratedCount ?maxCount)(neq ?*heat1* (str-cat ?iteratedNameDepartment " (" ?iteratedCount ")"))(neq ?*heat2* (str-cat ?iteratedNameDepartment " (" ?iteratedCount ")"))) then (bind ?finalNameDepartment ?iteratedNameDepartment)
            			(bind ?maxCount ?iteratedCount) ))
            (if (> ?maxCount 0) then (str-cat ?finalNameDepartment " (" ?maxCount ")") else "Undefined")
            (Department (heatingTurnedOn ?iteratedCount) (name ?iteratedNameDepartment)))
            
	?*cool1* <- (accumulate
            (progn (bind ?maxCount 0)(bind ?finalNameDepartment ""))
            (progn (if (>= ?iteratedCount ?maxCount) then (bind ?finalNameDepartment ?iteratedNameDepartment)
            			(bind ?maxCount ?iteratedCount) ))
            (if (> ?maxCount 0) then (str-cat ?finalNameDepartment " (" ?maxCount ")") else "Undefined")
            (Department (coolingTurnedOn ?iteratedCount) (name ?iteratedNameDepartment)))
            
    	?*cool2* <- (accumulate
            (progn (bind ?maxCount 0)(bind ?finalNameDepartment ""))
            (progn (if (and (>= ?iteratedCount ?maxCount)
            				(neq ?*cool1* (str-cat ?iteratedNameDepartment " (" ?iteratedCount ")"))) then 
            (bind ?finalNameDepartment ?iteratedNameDepartment)
            			(bind ?maxCount ?iteratedCount) ))
            (if (> ?maxCount 0) then (str-cat ?finalNameDepartment " (" ?maxCount ")") else "Undefined")
            (Department (coolingTurnedOn ?iteratedCount) (name ?iteratedNameDepartment)))
    
    ?*cool3* <- (accumulate
            (progn (bind ?maxCount 0)(bind ?finalNameDepartment ""))
            (progn (if (and (>= ?iteratedCount ?maxCount)
            				(neq ?*cool1* (str-cat ?iteratedNameDepartment " (" ?iteratedCount ")"))
            				(neq ?*cool2* (str-cat ?iteratedNameDepartment " (" ?iteratedCount ")")))  then 
            			(bind ?finalNameDepartment ?iteratedNameDepartment)
            			(bind ?maxCount ?iteratedCount) ))
            (if (> ?maxCount 0) then (str-cat ?finalNameDepartment " (" ?maxCount ")") else "Undefined")
            (Department (coolingTurnedOn ?iteratedCount) (name ?iteratedNameDepartment) {(heatingTurnedOn > 0) || (coolingTurnedOn > 0)}))
=>

;(facts)
    
)
