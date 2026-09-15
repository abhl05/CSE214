/*
 * ================================================================================
 * SOURCE: CSE 314 Sessional, 2022 batch, "Online on Behavioral Patterns"
 *         Subsection C1  |  Time allotted: 25 minutes
 * ================================================================================
 *
 * PROBLEM (restated in full so this file is self-sufficient):
 * ---------------------------------------------------------------------------
 * Build a small hospital visit simulator. Every patient visit must follow
 * the SAME overall flow, and the system must print each step in order:
 *   1. Check-In (register patient name + assign visit ID)
 *   2. Record Vitals (print temperature + blood pressure)
 *   3. Assessment
 *   4. Treatment
 *   5. Discharge Summary (print "patient discharged" + notes)
 *
 * The hospital has different departments; each department follows the SAME
 * visit structure above, but customizes only the Assessment and Treatment
 * steps:
 *   - General:    Assessment "Doctor performs normal diagnosis"
 *                 Treatment  "Prescribe standard medicine"
 *   - Pediatrics: Assessment "Doctor checks symptoms by ensuring child
 *                 comfort level"
 *                 Treatment  "Give child-safe medicine, friendly
 *                 reassurance message"
 *   - Emergency:  Assessment "Quick triage check (urgent/non-urgent)"
 *                 Treatment  "Immediate emergency procedure"
 *
 * Task: implement with the appropriate pattern and run the simulation for
 * one patient in each department, printing the flow clearly.
 *
 * ================================================================================
 * DESIGN PATTERN USED: TEMPLATE METHOD
 * ================================================================================
 *
 * WHY TEMPLATE METHOD IS THE CORRECT CHOICE:
 *   - The problem is phrased almost exactly like the Template Method
 *     definition: "every patient visit must follow the SAME overall flow"
 *     (a fixed algorithm skeleton with a fixed step ORDER), while "each
 *     department follows the same visit structure, but customizes a FEW
 *     STEPS" (Assessment and Treatment). Template Method is precisely
 *     about defining an algorithm's skeleton in a base class, and letting
 *     subclasses override individual steps without altering the
 *     algorithm's overall structure.
 *   - Check-In, Record Vitals, and Discharge Summary are IDENTICAL across
 *     every department -- these belong in the abstract base class as
 *     concrete (non-overridable, or optionally overridable) methods, so
 *     that behaviour is written exactly once and shared by all
 *     departments.
 *   - Assessment and Treatment vary per department but must still be
 *     invoked at the same fixed points in the sequence -- these become
 *     abstract "primitive operations" that each concrete department
 *     subclass must implement.
 *   - The template method itself (`performVisit()`) fixes the ORDER of
 *     steps once, in the base class, so no department can accidentally
 *     skip or reorder a step -- exactly satisfying "every visit must
 *     follow the same overall flow".
 *   - Rejected alternative: Strategy would fit if departments needed to
 *     swap out the ENTIRE algorithm as one interchangeable unit, or if the
 *     step sequence itself could vary. Here the sequence is fixed and
 *     shared, and only individual steps' implementations differ -- that
 *     is Template Method's defining distinction from Strategy.
 *
 * PARTICIPANTS (GoF roles -> classes in this file):
 *   AbstractClass     -> HospitalVisit (defines performVisit(), the
 *                          template method, plus concrete shared steps and
 *                          abstract steps)
 *   ConcreteClass      -> GeneralDepartmentVisit, PediatricsDepartmentVisit,
 *                          EmergencyDepartmentVisit (implement only the
 *                          abstract steps: assessment(), treatment())
 * ================================================================================
 */

// ---------------------------------------------------------------------------
// AbstractClass: defines the fixed skeleton of a hospital visit
// (`performVisit`, the Template Method) plus the steps shared by every
// department (Check-In, Record Vitals, Discharge Summary). Assessment and
// Treatment are left abstract for departments to specialise.
// ---------------------------------------------------------------------------
abstract class HospitalVisit {
    private String patientName;
    private int visitId;

    // --- The Template Method: fixes the ORDER of steps, once, for every
    // department. Declared `final` so no subclass can override the flow
    // itself -- only the individual steps below can be customised. ---
    public final void performVisit(String patientName, int visitId) {
        checkIn(patientName, visitId);
        recordVitals();
        assessment();   // varies by department (abstract step)
        treatment();    // varies by department (abstract step)
        dischargeSummary();
    }

    // Step 1 -- identical for every department, so implemented here.
    private void checkIn(String patientName, int visitId) {
        this.patientName = patientName;
        this.visitId = visitId;
        System.out.println("Step 1 [Check-In]: Registered patient '" + patientName +
                "' with visit ID #" + visitId);
    }

    // Step 2 -- identical for every department, so implemented here.
    // (Sample vitals are simulated since the problem does not require
    // real sensor input.)
    private void recordVitals() {
        System.out.println("Step 2 [Record Vitals]: Temperature 98.6°F, Blood Pressure 120/80 mmHg");
    }

    // Step 3 -- varies per department: each department provides its own
    // assessment message.
    protected abstract void assessment();

    // Step 4 -- varies per department: each department provides its own
    // treatment message.
    protected abstract void treatment();

    // Step 5 -- identical for every department, so implemented here.
    private void dischargeSummary() {
        System.out.println("Step 5 [Discharge Summary]: patient discharged. Notes: visit #" +
                visitId + " for " + patientName + " completed in the " + departmentName() + " department.");
    }

    // Small helper so the discharge note can mention the department name
    // without every department needing to reimplement dischargeSummary().
    protected abstract String departmentName();
}

// ---------------------------------------------------------------------------
// ConcreteClass #1
// ---------------------------------------------------------------------------
class GeneralDepartmentVisit extends HospitalVisit {
    @Override
    protected void assessment() {
        System.out.println("Step 3 [Assessment]: Doctor performs normal diagnosis");
    }

    @Override
    protected void treatment() {
        System.out.println("Step 4 [Treatment]: Prescribe standard medicine");
    }

    @Override
    protected String departmentName() { return "General"; }
}

// ---------------------------------------------------------------------------
// ConcreteClass #2
// ---------------------------------------------------------------------------
class PediatricsDepartmentVisit extends HospitalVisit {
    @Override
    protected void assessment() {
        System.out.println("Step 3 [Assessment]: Doctor checks symptoms by ensuring child comfort level");
    }

    @Override
    protected void treatment() {
        System.out.println("Step 4 [Treatment]: Give child-safe medicine, friendly reassurance message");
    }

    @Override
    protected String departmentName() { return "Pediatrics"; }
}

// ---------------------------------------------------------------------------
// ConcreteClass #3
// ---------------------------------------------------------------------------
class EmergencyDepartmentVisit extends HospitalVisit {
    @Override
    protected void assessment() {
        System.out.println("Step 3 [Assessment]: Quick triage check (urgent/non-urgent)");
    }

    @Override
    protected void treatment() {
        System.out.println("Step 4 [Treatment]: Immediate emergency procedure");
    }

    @Override
    protected String departmentName() { return "Emergency"; }
}

// ---------------------------------------------------------------------------
// Client / demo -- runs one patient visit through each department, showing
// that the overall flow (steps 1,2,5) is identical while steps 3 and 4
// differ.
// ---------------------------------------------------------------------------
public class C1_HospitalVisitSimulator {
    public static void main(String[] args) {
        System.out.println("=== General Department Visit ===");
        HospitalVisit generalVisit = new GeneralDepartmentVisit();
        generalVisit.performVisit("Rahim", 101);

        System.out.println("\n=== Pediatrics Department Visit ===");
        HospitalVisit pediatricsVisit = new PediatricsDepartmentVisit();
        pediatricsVisit.performVisit("Little Ayesha", 102);

        System.out.println("\n=== Emergency Department Visit ===");
        HospitalVisit emergencyVisit = new EmergencyDepartmentVisit();
        emergencyVisit.performVisit("Karim", 103);
    }
}
