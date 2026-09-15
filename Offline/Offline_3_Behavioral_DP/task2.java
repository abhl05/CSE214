import java.util.HashMap;
import java.util.Map;

interface Mediator {
    void registerStudent(Student student);
    void submitDepartmentConfirmation(String studentId);
    void issueOfficeOrder(String studentId);
    void issueTestimonial(String studentId);
    void issueCertificateAndTranscript(String studentId);
    void showStatus(String studentId);
    void registerOffice(String role, Collegue office);
}

enum ProcessingState {
    REGISTERED, DEPT_CONFIRMED, OFFICE_ORDER_ISSUED, TESTIMONIAL_ISSUED, COMPLETE
}

abstract class Collegue {
    protected final Mediator mediator;
    Collegue(Mediator mediator) {
        this.mediator = mediator;
    }
}

class DepartmentOffice extends Collegue {
    DepartmentOffice(Mediator mediator) {
        super(mediator);
    }

    public void confirmCompletion(String studentId) {
        System.out.println("[Department Office] Confirming academic completion for " + studentId);
        mediator.submitDepartmentConfirmation(studentId);
    }
}

class COEOffice extends Collegue {
    COEOffice(Mediator mediator) {
        super(mediator);
    }

    public void publishOfficeOrder(String studentId) {
        System.out.println("[Controller] Attempting to issue office order for " + studentId);
        mediator.issueOfficeOrder(studentId);
    }
 
    public void issueCertificateAndTranscript(String studentId) {
        System.out.println("[Controller] Attempting to issue certificate & transcript for " + studentId);
        mediator.issueCertificateAndTranscript(studentId);
    }
}

class DSW extends Collegue {
    DSW(Mediator mediator) {
        super(mediator);
    }

    public void issueTestimonial(String studentId) {
        System.out.println("[DSW] Attempting to issue testimonial for " + studentId);
        mediator.issueTestimonial(studentId);
    }
}

class Student extends Collegue {
    private String studentId;
    private String name;

    Student(Mediator mediator, String studentId, String name) {
        super(mediator);
        this.studentId = studentId;
        this.name = name;
    }

    void register() {
        System.out.println("Student " + studentId + " is registering.");
        mediator.registerStudent(this);
    }

    String getId() {
        return studentId;
    }

    String getName() {
        return name;
    }

    public void receiveNotification(String message) {
        System.out.println("   [Notify " + name + "] " + message);
    }
}

class ResultCoordinator implements Mediator {
    private final Map<String, Student> students = new HashMap<>();
    private final Map<String, ProcessingState> statusMap = new HashMap<>();
    private final Map<String, Collegue> registeredOffices = new HashMap<>();

    @Override
    public void registerOffice(String role, Collegue office) {
        registeredOffices.put(role, office);
        System.out.println("[Coordinator] Registered " + role);
    }
 
    @Override
    public void registerStudent(Student student) {
        students.put(student.getId(), student);
        statusMap.put(student.getId(), ProcessingState.REGISTERED);
        System.out.println("[Coordinator] Registered student " + student.getId() + " (" + student.getName() + ")");
    }
 
    @Override
    public void submitDepartmentConfirmation(String studentId) {
        if (!statusMap.containsKey(studentId)) {
            System.out.println("[Coordinator] ERROR: Student " + studentId + " is not registered.");
            return;
        }
        statusMap.put(studentId, ProcessingState.DEPT_CONFIRMED);
        System.out.println("[Coordinator] Departmental confirmation recorded for " + studentId);
    }
 
    @Override
    public void issueOfficeOrder(String studentId) {
        if (!statusMap.containsKey(studentId)) {
            System.out.println("[Coordinator] ERROR: Student " + studentId + " is not registered.");
            return;
        }
        ProcessingState state = statusMap.get(studentId);
        if (state != ProcessingState.DEPT_CONFIRMED) {
            System.out.println("[Coordinator] REJECTED: office order cannot be issued before departmental confirmation (" + studentId + ")");
            return;
        }
        statusMap.put(studentId, ProcessingState.OFFICE_ORDER_ISSUED);
        System.out.println("[Coordinator] Office order issued for " + studentId);
        students.get(studentId).receiveNotification("Your final-result office order has been issued.");
    }
 
    @Override
    public void issueTestimonial(String studentId) {
        if (!statusMap.containsKey(studentId)) {
            System.out.println("[Coordinator] ERROR: Student " + studentId + " is not registered.");
            return;
        }
        ProcessingState state = statusMap.get(studentId);
        if (state != ProcessingState.OFFICE_ORDER_ISSUED) {
            System.out.println("[Coordinator] REJECTED: testimonial cannot be issued before the office order (" + studentId + ")");
            return;
        }
        statusMap.put(studentId, ProcessingState.TESTIMONIAL_ISSUED);
        System.out.println("[Coordinator] Testimonial issued for " + studentId);
        students.get(studentId).receiveNotification("Your testimonial has been issued.");
    }
 
    @Override
    public void issueCertificateAndTranscript(String studentId) {
        if (!statusMap.containsKey(studentId)) {
            System.out.println("[Coordinator] ERROR: Student " + studentId + " is not registered.");
            return;
        }
        ProcessingState state = statusMap.get(studentId);
        if (state != ProcessingState.TESTIMONIAL_ISSUED) {
            System.out.println("[Coordinator] REJECTED: certificate/transcript cannot be issued before the testimonial (" + studentId + ")");
            return;
        }
        statusMap.put(studentId, ProcessingState.COMPLETE);
        System.out.println("[Coordinator] Certificate & transcript issued for " + studentId);
        students.get(studentId).receiveNotification("Your certificate and academic transcript are ready.");
    }

    @Override
    public void showStatus(String studentId) {
        System.out.println("[Status] " + studentId + " -> " + statusMap.get(studentId));
    }
}


public class Task2 {
    public static void main(String[] args) {
        Mediator coordinator = new ResultCoordinator();
 
        DepartmentOffice deptOffice = new DepartmentOffice(coordinator);
        COEOffice controller = new COEOffice(coordinator);
        DSW dsw = new DSW(coordinator);
        coordinator.registerOffice("Department Office", deptOffice);
        coordinator.registerOffice("Controller of Examinations", controller);
        coordinator.registerOffice("DSW", dsw);
 
        Student student = new Student(coordinator, "2005001", "Abhi");
        student.register();
 
        System.out.println("\n-- Step 1: Attempt to publish result before departmental confirmation --");
        controller.publishOfficeOrder(student.getId());
        coordinator.showStatus(student.getId());
 
        System.out.println("\n-- Step 2: Submission of departmental confirmation --");
        deptOffice.confirmCompletion(student.getId());
        coordinator.showStatus(student.getId());
 
        System.out.println("\n-- Step 3: Early attempt to issue certificate/transcript --");
        controller.issueCertificateAndTranscript(student.getId());
        coordinator.showStatus(student.getId());
 
        System.out.println("\n-- Step 4: Issuance of the final-result office order --");
        controller.publishOfficeOrder(student.getId());
        coordinator.showStatus(student.getId());
 
        System.out.println("\n-- Step 5: Issuance of the testimonial --");
        dsw.issueTestimonial(student.getId());
        coordinator.showStatus(student.getId());
 
        System.out.println("\n-- Step 6: Issuance of the certificate and transcript --");
        controller.issueCertificateAndTranscript(student.getId());
        coordinator.showStatus(student.getId());
 
        System.out.println("\n-- Step 7: Final status summary --");
        coordinator.showStatus(student.getId());
    }
}
