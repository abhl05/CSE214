/*
 * ================================================================================
 * SOURCE: CSE-214 Online, 2023 batch, "online3"  |  Duration: 30 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * A scheduler maintains a queue of tasks (Task ID, Start Time, End Time,
 * Priority HIGH/MEDIUM/LOW). Execution Time = End Time - Start Time.
 *
 * Three scheduling policies are supported:
 *   - FCFS: earliest Start Time first.
 *   - Priority: HIGH > MEDIUM > LOW; ties broken by earliest Start Time.
 *   - SJF: smallest Execution Time first; ties broken by earliest Start
 *     Time.
 *
 * The user picks a PREFERRED policy when the scheduler is created (and may
 * change it later), but the scheduler is ADAPTIVE: before selecting every
 * next task, it inspects the current waiting queue and decides which
 * policy to actually use right now:
 *   Rule 1 (Urgent Workload): if >= 1 waiting task is HIGH priority, use
 *     Priority Scheduling, regardless of the preferred policy.
 *   Rule 2 (Short-Task Workload): else if >= 3 waiting tasks have
 *     execution time <= 3, use SJF.
 *   Rule 3 (Normal Workload): otherwise, use the user's preferred policy.
 *
 * This decision is re-made after every task executes, so the ACTIVE policy
 * can change repeatedly while draining the same queue -- all WITHOUT
 * replacing the scheduler object itself.
 *
 * Required operations: addTask(...), setPreferredPolicy(...),
 * executeNextTask(), executeAll().
 *
 * ================================================================================
 * DESIGN PATTERN USED: STRATEGY
 * ================================================================================
 *
 * WHY STRATEGY IS THE CORRECT CHOICE:
 *   - FCFS, Priority Scheduling, and SJF are three interchangeable
 *     ALGORITHMS that all solve the same problem -- "given the waiting
 *     tasks, pick which one runs next" -- differing only in HOW they pick.
 *     That is exactly Strategy's purpose: encapsulate each algorithm
 *     behind a common interface (`SchedulingPolicy.selectNext(queue)`) so
 *     they are fully interchangeable from the caller's point of view.
 *   - The requirement "the scheduler should be able to perform these
 *     [policy] changes without replacing the scheduler object" is Strategy's
 *     signature capability: the Context (TaskScheduler) holds a reference
 *     to the currently ACTIVE SchedulingPolicy and simply reassigns that
 *     reference whenever the adaptive rules or the user's preference call
 *     for a different one -- the TaskScheduler object itself never changes
 *     identity.
 *   - The "adaptive" part (rules 1-3) is business logic that decides WHICH
 *     strategy to use, and that decision-making responsibility correctly
 *     lives in the Context (`determineActivePolicy()`), not inside the
 *     strategies themselves -- each concrete policy only knows how to
 *     select a task from a queue, exactly matching Strategy's separation
 *     of concerns between "the algorithm" and "who chooses/switches it".
 *   - Rejected alternative: State would apply if the SCHEDULER ITSELF had
 *     a lifecycle of internal conditions it moved through over time
 *     (e.g. "Idle -> Running -> Paused"). Here, instead, the "current
 *     policy" is recomputed FRESH from the queue's contents before every
 *     single task selection -- it is a data-driven algorithm choice made
 *     anew each time, not a persistent internal condition the object
 *     remembers and transitions between. That data-driven, recompute-every-
 *     time character is what makes this Strategy rather than State.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   Strategy          -> SchedulingPolicy (interface)
 *   ConcreteStrategy   -> FcfsPolicy, PriorityPolicy, SjfPolicy
 *   Context           -> TaskScheduler (holds the preferred policy,
 *                          re-evaluates the adaptive rules before every
 *                          selection, and delegates the actual task pick
 *                          to whichever ConcreteStrategy is currently
 *                          appropriate)
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// Supporting domain type.
// ---------------------------------------------------------------------------
enum TaskPriority { HIGH, MEDIUM, LOW }

class Task {
    private final String taskId;
    private final int startTime;
    private final int endTime;
    private final TaskPriority priority;

    public Task(String taskId, int startTime, int endTime, TaskPriority priority) {
        this.taskId = taskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priority = priority;
    }

    public String getTaskId() { return taskId; }
    public int getStartTime() { return startTime; }
    public int getExecutionTime() { return endTime - startTime; }
    public TaskPriority getPriority() { return priority; }

    @Override
    public String toString() {
        return taskId + "(start=" + startTime + ", end=" + endTime +
                ", priority=" + priority + ", execTime=" + getExecutionTime() + ")";
    }
}

// ---------------------------------------------------------------------------
// Strategy: common interface for every scheduling policy. Given the
// current waiting queue, returns which task should run next.
// ---------------------------------------------------------------------------
interface SchedulingPolicy {
    Task selectNext(java.util.List<Task> waitingQueue);
    String getName();
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #1: earliest Start Time first.
// ---------------------------------------------------------------------------
class FcfsPolicy implements SchedulingPolicy {
    @Override
    public Task selectNext(java.util.List<Task> waitingQueue) {
        Task best = waitingQueue.get(0);
        for (Task t : waitingQueue) {
            if (t.getStartTime() < best.getStartTime()) {
                best = t;
            }
        }
        return best;
    }

    @Override
    public String getName() { return "FCFS"; }
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #2: HIGH > MEDIUM > LOW, ties broken by earliest Start
// Time.
// ---------------------------------------------------------------------------
class PriorityPolicy implements SchedulingPolicy {
    // Lower number = more urgent, for easy comparison.
    private int rank(TaskPriority p) {
        switch (p) {
            case HIGH:   return 0;
            case MEDIUM: return 1;
            case LOW:
            default:     return 2;
        }
    }

    @Override
    public Task selectNext(java.util.List<Task> waitingQueue) {
        Task best = waitingQueue.get(0);
        for (Task t : waitingQueue) {
            if (rank(t.getPriority()) < rank(best.getPriority()) ||
                    (rank(t.getPriority()) == rank(best.getPriority()) && t.getStartTime() < best.getStartTime())) {
                best = t;
            }
        }
        return best;
    }

    @Override
    public String getName() { return "Priority Scheduling"; }
}

// ---------------------------------------------------------------------------
// ConcreteStrategy #3: smallest Execution Time first, ties broken by
// earliest Start Time.
// ---------------------------------------------------------------------------
class SjfPolicy implements SchedulingPolicy {
    @Override
    public Task selectNext(java.util.List<Task> waitingQueue) {
        Task best = waitingQueue.get(0);
        for (Task t : waitingQueue) {
            if (t.getExecutionTime() < best.getExecutionTime() ||
                    (t.getExecutionTime() == best.getExecutionTime() && t.getStartTime() < best.getStartTime())) {
                best = t;
            }
        }
        return best;
    }

    @Override
    public String getName() { return "SJF"; }
}

// ---------------------------------------------------------------------------
// Context: holds the waiting queue and the user's preferred policy.
// Before every single task selection, it re-evaluates the adaptive rules
// (1: urgent workload, 2: short-task workload, 3: normal workload) against
// the CURRENT queue contents to decide which ConcreteStrategy to delegate
// to -- all without ever replacing this TaskScheduler object.
// ---------------------------------------------------------------------------
class TaskScheduler {
    private static final int SHORT_TASK_THRESHOLD = 3;   // execTime <= 3
    private static final int SHORT_TASK_MIN_COUNT = 3;   // at least 3 such tasks

    private final java.util.List<Task> waitingQueue = new java.util.ArrayList<>();
    private SchedulingPolicy preferredPolicy;

    // Pre-built strategy instances -- reused every time, never replaced.
    private final SchedulingPolicy fcfs = new FcfsPolicy();
    private final SchedulingPolicy priority = new PriorityPolicy();
    private final SchedulingPolicy sjf = new SjfPolicy();

    public TaskScheduler(SchedulingPolicy preferredPolicy) {
        this.preferredPolicy = preferredPolicy;
    }

    public void addTask(Task task) {
        waitingQueue.add(task);
    }

    public void setPreferredPolicy(SchedulingPolicy preferredPolicy) {
        this.preferredPolicy = preferredPolicy;
        System.out.println("Preferred policy changed to: " + preferredPolicy.getName());
    }

    // Implements Rules 1-3 of the adaptive scheduling logic, using the
    // CURRENT contents of the waiting queue.
    private SchedulingPolicy determineActivePolicy() {
        boolean hasHighPriorityTask = waitingQueue.stream()
                .anyMatch(t -> t.getPriority() == TaskPriority.HIGH);
        if (hasHighPriorityTask) {
            return priority; // Rule 1: Urgent Workload
        }

        long shortTaskCount = waitingQueue.stream()
                .filter(t -> t.getExecutionTime() <= SHORT_TASK_THRESHOLD)
                .count();
        if (shortTaskCount >= SHORT_TASK_MIN_COUNT) {
            return sjf; // Rule 2: Short-Task Workload
        }

        return preferredPolicy; // Rule 3: Normal Workload
    }

    public void executeNextTask() {
        if (waitingQueue.isEmpty()) {
            System.out.println("No tasks waiting -- nothing to execute.");
            return;
        }

        SchedulingPolicy activePolicy = determineActivePolicy();
        Task selected = activePolicy.selectNext(waitingQueue);
        waitingQueue.remove(selected);

        System.out.println("Executing " + selected + " using policy: " + activePolicy.getName());
    }

    public void executeAll() {
        while (!waitingQueue.isEmpty()) {
            executeNextTask();
        }
    }
}

// ---------------------------------------------------------------------------
// Client / demo -- reproduces the example scenario from the problem
// statement, showing the active policy switching automatically as the
// queue's contents change.
// ---------------------------------------------------------------------------
public class Online3_AdaptiveTaskScheduler {
    public static void main(String[] args) {
        TaskScheduler scheduler = new TaskScheduler(new FcfsPolicy()); // preferred policy: FCFS

        scheduler.addTask(new Task("T1", 0, 8, TaskPriority.MEDIUM));
        scheduler.addTask(new Task("T2", 1, 4, TaskPriority.LOW));
        scheduler.addTask(new Task("T3", 2, 4, TaskPriority.MEDIUM));
        scheduler.addTask(new Task("T4", 3, 4, TaskPriority.LOW));
        scheduler.addTask(new Task("T5", 4, 9, TaskPriority.HIGH));

        // As tasks are removed, the workload conditions change, so the
        // scheduler may pick a different policy for each subsequent task
        // -- entirely automatically, and entirely within this same
        // TaskScheduler instance.
        scheduler.executeAll();
    }
}
