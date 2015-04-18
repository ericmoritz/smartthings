def FSM(oldfsm) {
  oldfsm ?: [:]
}

/**********/
/* Events */
/**********/
def FSM_setTemp(fsm, dayOfWeek, hour, temp) {
  def key = [dayOfWeek, hour]
  def entry = fsm[key] ?: ["total": 0, "count": 0, "mean": 0]
  entry["total"] += temp
  entry["count"] += 1
  fsm[key] = entry
  fsm
}
/***********/
/* Queries */
/***********/
def FSM_suggestTemp(fsm, dayOfWeek, hour) {
  def key = [dayOfWeek, hour]
  def entry = fsm[key]
  if(entry != null) {
    (entry["total"] / entry["count"]).longValue()
  }
}
