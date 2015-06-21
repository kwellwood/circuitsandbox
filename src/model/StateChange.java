/*
 * StateChange.java
 *
 * Created on March 10, 2005, 8:18 PM
 */

package model;

/**
 * <p>Encapsulates the information necessary to change the output value of a
 * component. <code>StateChange</code> objects are created by logic components
 * when an output value needs to changed.</p>
 * 
 * <p>All <code>StateChange</code> objects are placed in a <code>TreeSet</code>
 * collection and sorted by a timestamp (secondarily by id). When the system
 * clock reaches the time stamp of a <code>StateChange</code> object, the
 * change is then applied to the associated component's output.</p>
 *
 * @author Daniel Stahl
 * @author Kevin Wellwood
 */
public class StateChange implements Comparable {
    
    /**
     * Constructs a new instance of <code>StateChange</code> which should be
     * executed at the specified time.
     *
     * @param logicComponent the logic component to change
     * @param outputNumber the output number of the logic component
     * @param newValue the value to give the output
     * @param timeStamp the time (in millis) when the change shold be made
     */
    public StateChange(LogicComponent logicComponent, int outputNumber,
            byte newValue, long timeStamp) {
        this.logicComponent = logicComponent;
        this.outputNumber = outputNumber;
        this.newValue = newValue;
        this.timeStamp = timeStamp;
        this.id = nextId;
        nextId++;
    }
    
    /**
     * Sets the new value of the target output on a logic component.
     */
    public void execute() {
        logicComponent.setValueOfOutput(outputNumber, newValue);
    }
    
    /**
     * Returns the time when this <code>StateChange</code> should be executed
     * in milliseconds.
     *
     * @return the time stamp
     */
    public long getTimeStamp() {
        return timeStamp;
    }
    
    /**
     * Compares the timestamp of this <code>StateChange</code> to another's. If
     * the timestamps are equal, the id numbers are compared. The object with
     * the lower id number is ordered first.
     *
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object
     */
    public int compareTo(Object o) {
        StateChange sc = (StateChange)o;
        if (id == sc.id) {
            return 0;
        } else if (id > sc.id) {
            return 1;
        } else {
            return -1;
        }
    }
    
    /** the id number of the StateChange */
    private long id;
    /** the system time in milliseconds when this <code>StateChange</code>
     * should be run */
    private long timeStamp;
    /** the logic component to change */
    private LogicComponent logicComponent;
    /** the output number of the logic component to change */
    private int outputNumber;
    /** the value to set the output to */
    private byte newValue;
    
    /** id number to assign to the next StateChange object created */
    private static long nextId = Long.MIN_VALUE;
}
