# Reflection

La API reflection se trata de leer y modificar el contenido de un objeto en tiempo de ejecución sin ningún conocimiento previo de la estructura o incluso la clase de ese objeto en el momento de la compilación. De hecho, la API de reflection trata de descubrir el contenido de un objeto y la estructura de la clase en tiempo de ejecución, que es una funcionalidad muy potente.

Hay varias clases fundamentales que se deben conocer en está API y el nombre esas clases es el siguiente, hay una clase llamada ‘Class’, que puede ser confuso, pero este es el caso. También una clase llamada ‘Field’ para modelar los campos dentro de una clase y una clase llamada ‘Method’ para modelar métodos, nuevamente dentro de una clase y hay una clase adicional llamada la clase ‘Constructor’, un constructor en la clase no es realmente un método, incluso si parece un método y los constructores se modelan utilizando su propia clase. También se usaría la clase ‘Annotation’ que modela las anotaciones.

```java
String hello = "Hello";
Class helloClass = hello.getClass();
Class<?> helloClass = "Hello".getClass();
// la razón de esto radica en la forma en que los genéricos funcionan en java. De echo Class of String o Class of Object no son extensiones de Class of question mark.
Class<String> helloClass = "Hello".getClass(); // compile error.
Class<? extends String> helloClass = "Hello".getClass(); // is correct.
// obtiene una referencia  de la clase misma.
Class<?> stringClass = String.class;
// recomienda porque Brinda muchas oportunidades de la reflection.
String className = "java.lang.String";
Class<?> stringClass = Class.forName(className);
// forName puede producir varias excepciones por casos de seguridad y también puede ser por falta de derechos.
```

## Obtener los campos de una clase

Hay muchos métodos en la clase llamada ‘Class’, son métodos para obtener referencias en los campos, para obtener referencias en los métodos, en constructores y mucha más información.

```java
Class<?> getClass = Person.class;
Field field = getClass.getField("age");
// los campos declarados son los campos declarados dentro de esa clase, ya sean privados, protegidos, protegidos por paquetes o públicos.
Field[] declaredFields = getClass.getDeclaredFields();
// Son los campos públicos de la clase, incluidos de la super clase.
Field[] fields = getClass.getFields();
```

## Obtener los métodos de una clase

Una de las formas de obtener los métodos es usando el método getMethod(), devolverá un solo método, pasamos el nombre del método y luego los tipos de parámetros en el orden correcto. Si este método toma varios parámetros, luego agregamos los otros tipos de los otros parámetros después del primero. También hay un método GetDeclaredMethod() que devolverá todos los métodos dentro de esa clase ya sean públicos, protegidos o privados y un método getMethods() que devolverá todos los métodos públicos declarados en esa clase y todos los super clases de esa clase.

```java
Class<?> getClass = Person.class;
Method method = getClass.getMethod("setName", String.class);
Method[] declaredMethods = getClass.getDeclaredMethods();
Method[] methods = getClass.getMethods();
```

## Obtener los constructores de una clase

Para obtener los constructores de una clase, un constructor puede ser visto como una especie de método, pero de hecho no lo es y dentro del contexto de la API de reflection, esta modelado por una clase que es la clase constructora ‘Constructor’. El primer método es el mismo que el método o los campos. Es solo un método getConstructor() que toma los tipos de parámetros tomados por ese constructor.

```java
Class<?> getClass = Person.class;
Constructor constructor = getClass.getConstructor(Class<?>… types);
Constructor[] declaredConstructors = getClass.getDeclaredConstructors();
//Los constructores de la super clase no estarán incluidos, es la diferencia con los métodos y campos.
Constructor[] constructors = getClass.getConstructos();
```

## Accesibilidad

Si el campo dado es privado entonces habría una excepción que es una excepción IllegalAccessException porque no hay permiso para leer un campo privado desde fuera de la clase ‘Person’, entonces la encapsulación no está rota y si hay una verificación de seguridad para acceder a un miembro privado a una clase Reflection, hay un método en la clase ‘Field’ que se llama setAccessible(). La llamada a setAccessible() en true realmente hace que suprima el control de acceso en ese campo.

```java
Person o = …;
Class<?> getClass = o.getClass();
Field field = getClass.getDeclaredField("name");
field.setAccessible(true);
field.setValue(o, "Sarah");
String name = (String)field.getValue(o);
```

## Anotaciones

```java
public class Person {
    @PrimaryKey
    private long id;
    @Column
    private int age;
    @Column
    private String name;

    // getters and setters
}

public @interface PrimaryKey { }
public @interface Column { }
```

En la forma como funcionan las anotaciones en java, el compilador primero maneja una anotación y el compilador debe decidir en que momento del ciclo de vida de una clase estará disponible una anotación. Hay tres etapas cuando una anotación puede estar disponible.

* La primera etapa es la propia etapa de compilación, por lo que solo el compilador podrá ver esa anotación si elegimos hacerlo.
* La segunda etapa es la carga de clases por lo que la anotación solo verá el cargador de clases.
* La tercera es el propio tiempo de ejecución.

## Inyección de dependencias

Si se está diseñando un objeto que necesita otro objeto para funcionar, un objeto delegado, entonces este primer objeto no debe intentar crear el objeto delegado, en lugar de eso cuando se construye el objeto, debería intentar pedir algún otro mecanismo para inyectar ese objeto delegado en el primer objeto. Entonces una forma de implementar esto es trabajar con un framework de inyección de dependencias, no es la única forma de implementar, pero probablemente sea la más clásica en las aplicaciones empresariales.

![Alt text](/Reflection/assets/dependenciesInjection.png?raw=true "Database Connection")

```java
BeanManager beanManager = BeanManager.getInstance();
EntityManager entityManager = beanManager.get(EntityManager.class);
```

## Invocación de métodos

```java
public class ConnectionProvider {
  Connection createConnection(String url) { ... }
}

Class<?> connectionType = ConnectionProvider.class;
Object connectionProvider = connectionType.getConstructor().getInstance();
Method method = connectionType.getMethod("createConnection", String.class);
method.invoke(connectionProvider, "jdbc:h2:mem:db_reflection");
```