package com.github.mykyta.sirobaba.ailearningtracker.events.goal;


/**
 * Event triggered when a subgoal is completed.
 * <p>
 * Contains the ID of the parent goal to which the completed subgoal belongs.
 * This event can be used to perform actions such as updating the parent goal's
 * completion status or notifying other parts of the system.
 * </p>
 *
 * @param parentGoalId the ID of the parent goal associated with the completed subgoal
 *
 * <p>
 * Created by Mykyta Sirobaba on 28.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
public record SubgoalCompletedEvent(Long parentGoalId) {
}
