import java.util.HashMap;
import java.util.Map;

interface Mediator {
    void registerStudent(Student student);
    void submitDepartmentConfirmation(String studentId);
    void issueOfficeOrder(String studentId);
    void issueTestimonial(String studentId);
    void issueCertificateAndTranscript(String studentId);
    void showStatus(String studentId);
}

enum ProcessingState {
    REGISTERED, DEPT_CONFIRMED, OFFICE_ORDER_ISSUED, TESTIMONIAL_ISSUED, COMPLETE
}

abstract class Collegue {
    protected final ResultCoordinator mediator;
    Collegue(ResultCoordinator mediator) {
        this.mediator = mediator;
    }
}

class DepartmentOffice extends Collegue {
    DepartmentOffice(ResultCoordinator mediator) {
        super(mediator);
    }

    public void confirmCompletion(String studentId) {
        System.out.println("[Department Office] Confirming academic completion for " + studentId);
        mediator.submitDepartmentConfirmation(studentId);
    }
}

class COEOffice extends Collegue {
    COEOffice(ResultCoordinator mediator) {
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
    DSW(ResultCoordinator mediator) {
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

    Student(ResultCoordinator mediator, String studentId, String name) {
        super(mediator);
        this.studentId = studentId;
        this.name = name;
    }

    String getStudentId() {
        return studentId;
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
 
    @Override
    public void registerStudent(Student student) {
        students.put(student.getId(), student);
        statusMap.put(student.getId(), ProcessingState.REGISTERED);
        System.out.println("[Coordinator] Registered student " + student.getId() + " (" + student.getName() + ")");
    }
 
    @Override
    public void submitDepartmentConfirmation(String studentId) {
        statusMap.put(studentId, ProcessingState.DEPT_CONFIRMED);
        System.out.println("[Coordinator] Departmental confirmation recorded for " + studentId);
    }
 
    @Override
    public void issueOfficeOrder(String studentId) {
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


public class task2 {
    public static void main(String[] args) {
        ResultCoordinator coordinator = new ResultCoordinator();
 
        DepartmentOffice deptOffice = new DepartmentOffice(coordinator);
        COEOffice controller = new COEOffice(coordinator);
        DSW dsw = new DSW(coordinator);
 
        Student student = new Student(coordinator, "2005001", "Abhi");
        coordinator.registerStudent(student);
 
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
