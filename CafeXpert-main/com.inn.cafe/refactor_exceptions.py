import os
import glob
import re

src_dir = r"c:/Users/shiva/Downloads/CafeXpert-main/CafeXpert-main/com.inn.cafe/src/main/java/com/inn/cafe"

for root, dirs, files in os.walk(src_dir):
    for filename in files:
        if filename.endswith(".java") and ("Rest" in filename or "Service" in filename):
            if "Category" in filename: continue # already done manually
            filepath = os.path.join(root, filename)
            with open(filepath, 'r') as f:
                content = f.read()
            
            original_content = content
            
            # Add import
            if "import com.inn.cafe.exception.BaseException;" not in content:
                content = content.replace("public interface", "import com.inn.cafe.exception.BaseException;\n\npublic interface")
                content = content.replace("@Service\npublic class", "import com.inn.cafe.exception.BaseException;\n\n@Service\npublic class")
                content = content.replace("@RestController\npublic class", "import com.inn.cafe.exception.BaseException;\n\n@RestController\npublic class")
                content = content.replace("public class", "import com.inn.cafe.exception.BaseException;\n\npublic class")

            # Interface methods
            content = re.sub(r'(ResponseEntity<.*?>\s+\w+\(.*?\))\s*;', r'\1 throws Exception;', content)
            
            # Class methods
            content = re.sub(r'(ResponseEntity<.*?>\s+\w+\(.*?\))\s*\{', r'\1 throws Exception {', content)
            
            # Catch blocks
            old_catch = r'\}\s*catch\s*\(\s*Exception\s+e\s*\)\s*\{[\s\S]*?e\.printStackTrace\(\);[\s\S]*?\}'
            new_catch = '} catch (BaseException e) {\n            throw new BaseException(e.getMessage(), e.getStatusCode());\n        } catch (Exception e) {\n            e.printStackTrace();\n            throw new Exception(e.getMessage());\n        }'
            
            content = re.sub(old_catch, new_catch, content)
            
            # Check exceptions thrown already to avoid duplicates (throws Exception throws Exception)
            content = content.replace("throws Exception throws Exception", "throws Exception")
            content = content.replace("throws Exception  throws Exception", "throws Exception")
            
            if original_content != content:
                with open(filepath, 'w') as f:
                    f.write(content)
                print(f"Updated {filename}")
