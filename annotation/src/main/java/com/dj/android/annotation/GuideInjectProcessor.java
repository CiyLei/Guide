package com.dj.android.annotation;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class GuideInjectProcessor extends AbstractProcessor {

    private String GuidePackage = "com.dj.android.library";
    private Filer mFileUtils;
    private Elements mElementUtils;
    private Messager mMessager;

    Map<String, List<VariableInfo>> classMap = new HashMap<>();
    Map<String, TypeElement> classTypeElement = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFileUtils = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotationTypes = new LinkedHashSet<String>();
        annotationTypes.add(GuideBindView.class.getCanonicalName());
        return annotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        collectInfo(roundEnv);
        writeToFile();
        return true;
    }

    /**
     * 收集注解信息
     * @param roundEnv
     */
    private void collectInfo(RoundEnvironment roundEnv) {
        classMap.clear();
        classTypeElement.clear();

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(GuideBindView.class);
        for (Element element : elements) {
            int id = element.getAnnotation(GuideBindView.class).id();
            String description = element.getAnnotation(GuideBindView.class).value();
            String guideViewClass = element.getAnnotation(GuideBindView.class).guideView();
            GuidePackage = element.getAnnotation(GuideBindView.class).guideViewPackage();

            VariableElement variableElement = (VariableElement) element;
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            String classFullName = typeElement.getQualifiedName().toString();

            List<VariableInfo> variableList = classMap.get(classFullName);
            if (variableList == null) {
                variableList = new ArrayList<>();
                classMap.put(classFullName, variableList);
                classTypeElement.put(classFullName, typeElement);
            }
            VariableInfo variableInfo = new VariableInfo();
            variableInfo.setVariableElement(variableElement);
            variableInfo.setId(id);
            variableInfo.setDescription(description);
            variableInfo.setGuideView(guideViewClass);
            variableList.add(variableInfo);
        }
    }

    /**
     * 生成java文件
     */
    private void writeToFile() {
        for (String key : classMap.keySet()) {
            List<VariableInfo> vs = classMap.get(key);
            TypeElement typeElement = classTypeElement.get(key);

            try {
                JavaFileObject sourceFile = mFileUtils.createSourceFile(typeElement.getSimpleName() + "$$GuideInject", typeElement);
                Writer writer = sourceFile.openWriter();
                writer.write(generateJavaCode(typeElement, vs));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * java源码
     * @param typeElement
     * @param vs
     * @return
     */
    private String generateJavaCode(TypeElement typeElement, List<VariableInfo> vs) {
        String packageFullName = mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
        StringBuilder builder = new StringBuilder();
        builder.append("package " + packageFullName).append(";\n\n");
        builder.append("import android.content.Context;\n");
        builder.append("import android.view.View;\n");
        builder.append("import " + GuidePackage + ".Guide;\n");
        builder.append("import " + GuidePackage + ".GuideManager;\n\n");
        builder.append("import " + GuidePackage + ".GuideView;\n\n");
        builder.append("public class " + typeElement.getSimpleName() + "$$GuideInject" + " {\n\n");
        builder.append("    public static void show(Context context, GuideManager.Listener listener) {\n");
        builder.append("        Guide.Builder()\n");
        for (VariableInfo v : vs) {
            builder.append("            .add(new " + v.getGuideView() + "(" + v.getId() + ","//v.getGuideView().getSimpleName()
                    + "((" + typeElement.getSimpleName() + " )context)." + v.getVariableElement().getSimpleName().toString()
                    + ",\"" + v.getDescription() + "\"))\n");
        }
        builder.append("            .show(context, listener);\n");
        builder.append("    }\n\n");
        builder.append("    public static void show(Context context) {\n");
        builder.append("        Guide.Builder()\n");
        for (VariableInfo v : vs) {
            builder.append("            .add(new " + v.getGuideView() + "(" + v.getId() + ","//v.getGuideView().getSimpleName()
                    + "((" + typeElement.getSimpleName() + " )context)." + v.getVariableElement().getSimpleName().toString()
                    + ",\"" + v.getDescription() + "\"))\n");
        }
        builder.append("            .show(context);\n");
        builder.append("    }\n\n");
        if (vs.size() > 0) {

            builder.append("    public static void show(Context context, GuideManager.Listener listener, int emptyCount, Class<? extends GuideView> guideViewClass) {\n");
            builder.append("        Guide.Builder builder = Guide.Builder();\n");
            builder.append("        builder\n");
            for (VariableInfo v : vs) {
                builder.append("            .add(new " + v.getGuideView() + "(" + v.getId() + ","//v.getGuideView().getSimpleName()
                        + "((" + typeElement.getSimpleName() + " )context)." + v.getVariableElement().getSimpleName().toString()
                        + ",\"" + v.getDescription() + "\"))\n");
            }
            builder.append("        ;\n");
            builder.append("        for (int i = 0; i < emptyCount; i++) {\n");
            builder.append("        GuideView gv = null;\n");
            builder.append("            try {\n");
            builder.append("                gv = guideViewClass.getConstructor(int.class, View.class, String.class).newInstance(101 + i, null, \"\");\n");
            builder.append("            } catch (Exception e) {\n");
            builder.append("                e.printStackTrace();\n");
            builder.append("            }\n");
            builder.append("            if (gv != null) {\n");
            builder.append("                builder.add(gv);\n");
            builder.append("            }\n");
            builder.append("        }\n");
            builder.append("        builder.show(context, listener);\n");
            builder.append("    }\n\n");

            builder.append("    public static void show(Context context, GuideManager.Listener listener, int[] emptyIds, Class<? extends GuideView> guideViewClass) {\n");
            builder.append("        Guide.Builder builder = Guide.Builder();\n");
            builder.append("        builder\n");
            for (VariableInfo v : vs) {
                builder.append("            .add(new " + v.getGuideView() + "(" + v.getId() + ","//v.getGuideView().getSimpleName()
                        + "((" + typeElement.getSimpleName() + " )context)." + v.getVariableElement().getSimpleName().toString()
                        + ",\"" + v.getDescription() + "\"))\n");
            }
            builder.append("        ;\n");
            builder.append("        for (int id : emptyIds) {\n");
            builder.append("            GuideView gv = null;\n");
            builder.append("            try {\n");
            builder.append("                gv = guideViewClass.getConstructor(int.class, View.class, String.class).newInstance(id, null, \"\");\n");
            builder.append("            } catch (Exception e) {\n");
            builder.append("                e.printStackTrace();\n");
            builder.append("            }\n");
            builder.append("            if (gv != null) {\n");
            builder.append("                builder.add(gv);\n");
            builder.append("            }\n");
            builder.append("        }\n");
            builder.append("        builder.show(context, listener);\n");
            builder.append("    }\n\n");
            builder.append("}");
        }
        return builder.toString();
    }
}
