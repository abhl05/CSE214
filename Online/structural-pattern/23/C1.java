import java.util.Base64;
import java.util.List;

// ================= Internal service (unchanged, cannot touch) =================
class LegacyInternalXMLService {
    String getDataAsXML() {
        return "<user><id>101</id><name>Ishrat</name></user>";
    }
}

// ========= Target interface (what client code is written against) =========
interface ApiResponse {
    String getBody();
}

// ================= ADAPTER =================
// Bridges the legacy XML producer to the JSON-shaped interface the third party expects.
// LegacyInternalXMLService is never modified — this class just wraps it.
class XmlToJsonAdapter implements ApiResponse {
    private LegacyInternalXMLService legacyService;

    public XmlToJsonAdapter(LegacyInternalXMLService legacyService) {
        this.legacyService = legacyService;
    }

    @Override
    public String getBody() {
        String xml = legacyService.getDataAsXML();
        return convertXmlToJson(xml);
    }

    // Minimal XML->JSON conversion for this fixed shape.
    // (In a real system you'd use a proper XML parser + JSON library.)
    private String convertXmlToJson(String xml) {
        String id = extractTag(xml, "id");
        String name = extractTag(xml, "name");
        return "{\"id\":" + id + ",\"name\":\"" + name + "\"}";
    }

    private String extractTag(String xml, String tag) {
        String open = "<" + tag + ">";
        String close = "</" + tag + ">";
        int start = xml.indexOf(open) + open.length();
        int end = xml.indexOf(close);
        return xml.substring(start, end);
    }
}

// ================= DECORATOR =================
// Base decorator: wraps any ApiResponse (the adapter's output, or another decorator's output).
// This is what makes combinations and ordering possible without a class per combination.
abstract class ResponseDecorator implements ApiResponse {
    protected ApiResponse wrapped;

    public ResponseDecorator(ApiResponse wrapped) {
        this.wrapped = wrapped;
    }
}

class EncryptionDecorator extends ResponseDecorator {
    public EncryptionDecorator(ApiResponse wrapped) {
        super(wrapped);
    }

    @Override
    public String getBody() {
        String body = wrapped.getBody();
        // Simulated encryption (Base64 stands in for a real cipher here)
        return "ENC[" + Base64.getEncoder().encodeToString(body.getBytes()) + "]";
    }
}

class CompressionDecorator extends ResponseDecorator {
    public CompressionDecorator(ApiResponse wrapped) {
        super(wrapped);
    }

    @Override
    public String getBody() {
        String body = wrapped.getBody();
        // Simulated compression (just tagging + shrinking marker for demo purposes)
        return "GZIP[" + body.length() + "b:" + body + "]";
    }
}

// A future transform (e.g. digital signature) would just be another class here —
// no changes needed to EncryptionDecorator, CompressionDecorator, or the adapter.
// class SignatureDecorator extends ResponseDecorator { ... }

// ================= PIPELINE BUILDER =================
// Reads per-request params and builds the decorator chain in the order the client asked for.
// This is what avoids needing a separate class per combination (Encrypt+Compress,
// Compress+Encrypt, Encrypt-only, Compress-only, neither — all handled by one mechanism).
class ResponseBuilder {
    public static ApiResponse build(ApiResponse base, List<String> transformOrder) {
        ApiResponse response = base;
        for (String transform : transformOrder) {
            switch (transform) {
                case "encrypt":
                    response = new EncryptionDecorator(response);
                    break;
                case "compress":
                    response = new CompressionDecorator(response);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown transform: " + transform);
            }
        }
        return response;
    }
}

public class Main {
    public static void main(String[] args) {
        LegacyInternalXMLService legacyService = new LegacyInternalXMLService();
        ApiResponse jsonResponse = new XmlToJsonAdapter(legacyService);

        System.out.println("Raw XML (untouched internal format):");
        System.out.println(legacyService.getDataAsXML());
        System.out.println();

        // ---- Request 1: ?compress=true&encrypt=true, order = compress THEN encrypt ----
        System.out.println("compress-then-encrypt (?compress=true&encrypt=true):");
        ApiResponse r1 = ResponseBuilder.build(jsonResponse, List.of("compress", "encrypt"));
        System.out.println(r1.getBody());
        System.out.println();

        // ---- Request 2: same params but order = encrypt THEN compress ----
        System.out.println("encrypt-then-compress:");
        ApiResponse r2 = ResponseBuilder.build(jsonResponse, List.of("encrypt", "compress"));
        System.out.println(r2.getBody());
        System.out.println();

        // ---- Request 3: only encryption ----
        System.out.println("encrypt only (?encrypt=true):");
        ApiResponse r3 = ResponseBuilder.build(jsonResponse, List.of("encrypt"));
        System.out.println(r3.getBody());
        System.out.println();

        // ---- Request 4: neither ----
        System.out.println("no transforms (?):");
        ApiResponse r4 = ResponseBuilder.build(jsonResponse, List.of());
        System.out.println(r4.getBody());
    }
}