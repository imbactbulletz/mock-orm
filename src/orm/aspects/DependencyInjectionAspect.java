package orm.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import orm.annotations.ManyToOne;
import orm.annotations.OneToMany;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Aspect
public class DependencyInjectionAspect {


    @Pointcut("@annotation(orm.annotations.Entity)")
    public void atEntity(){}

    @Pointcut("execution(* *(..))")
    public void atExecution(){
    }

    @Before("execution(* (@orm.annotations.Entity *).*(..))")
    public void initializeFields(JoinPoint pointcut){
       // object that the pointcut is called upon
       Object targetObject = pointcut.getTarget();
       // class of the target object
       Class targetObjectClass;

       try {
           targetObjectClass = targetObject.getClass();
       }
       catch(NullPointerException npe){
           return;
       }
       // field of target object's class
       Field[] targetClassFields = targetObjectClass.getDeclaredFields();


       // looking for fields that are annotated with OneTomany or ManyToOne and initializing their values
       for(Field field: targetClassFields){
           Annotation[] annotations = field.getDeclaredAnnotations();


           for(Annotation annotation : annotations){

                   field.setAccessible(true);

                   try {
                       Object fieldValue = field.get(targetObject);

                       if(fieldValue == null){

                           // checking if the type's generic
                           Type type = field.getGenericType();

                           // oh yes, it is
                           if(type instanceof ParameterizedType){
                               ParameterizedType parameterizedType = (ParameterizedType) type;

                               // looking for the wrapping type - can be List, ArrayList, etc.
                               Type rawType = parameterizedType.getRawType();

                               String className = null;

                               // checking if it's a List
                               if(rawType.getTypeName().contains("java.util.List")){
                                   // it is a list, since we can't initialize it as a List, we'll initialize it as an ArrayList
                                   className = "java.util.ArrayList";
                               }


                               // gets class of ArrayList
                               Class cls = Class.forName(className);

                               // initializes field with a new ArrayList object
                               fieldValue = cls.getDeclaredConstructor().newInstance();
                               field.set(targetObject, fieldValue);
                           } else {
                             // not a generic type

                             // getting type's class
                             Class cls = Class.forName(type.getTypeName());
                             // initializing the field value
                               fieldValue = cls.getDeclaredConstructor().newInstance();
                               field.set(targetObject, fieldValue);
                           }

                       }
                   } catch (IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
                       e.printStackTrace();
                   }
               }


       }
    }
}
