# apache_pdfbox_corretto
Amazon Correttoを使えるDockerを使ってApache PDFBoxでPDFファイルのテキスト抽出を試します。<br>
[Apache PDFBox - A Java PDF Library](https://pdfbox.apache.org/)<br>

# Amazon CorrettoをDockerで動かす方法
Apache PDFBoxでテストする必要があり、Javaの環境が必要だったので、Amazon Correttoを使うことにしました。<br>
手順をすぐに忘れてしまうので、これは備忘録です。

## 前提条件
1. Macを使っています。
1. Docker Desktop for Macがインストールされていること。
1. Amazon CorrettoのDockerイメージを使用するための基本的なコマンドライン操作が可能であること。

Finderから「Docker.app」を起動し、Dockerが正常に動作していることを確認。<br>
ターミナルで以下のコマンドを実行し、Dockerがインストールされているか確認。<br>
```
docker --version
```

## Amazon Correttoの公式イメージを取得
- Docker Hubには、Amazon Correttoの公式イメージが提供されています。
- 以下のコマンドで必要なバージョンのCorrettoイメージを取得します。

Amazon Corretto 11の場合
```
docker pull amazoncorretto:11
```
### コンテナを起動
ダウンロードしたイメージを使ってコンテナを起動します。
```
docker run -it --name corretto-container amazoncorretto:11 /bin/bash
```
このコマンドで、`/bin/bash`シェルに入ります。<br>
コンテナ内でJava環境が利用可能です。<br>
コンテナ内で以下のコマンドを実行し、Java環境が正しく設定されているか確認します。<br>
```
java -version
```
sudoのインストール方法：
もし`sudo`がインストールされていない場合、以下の手順でインストールします。<br>
Amazon Linux 2の場合：
- `yum`を使用してインストール：
```
yum install -y sudo
```
```
sudo --version
```

tarをインストール：
```
sudo yum install tar
```

### Apache Mavenのインストール
bashを使ってLinux環境にMavenをインストールする手順です。<br>
OSの名前やバージョン情報を確認します。<br>
```
cat /etc/os-release
```
Linuxディストリビューションの詳細確認します。<br>
```
lsb_release -a
```
Javaのインストールを確認します。
```
java -version
echo $JAVA_HOME
```
/optに公式サイトからMavenをダウンロードします。
```
curl -OL https://downloads.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz
```
ダウンロードしたアーカイブを解凍し、`/opt`に配置します。<br>
シンボリックリンク `/opt/maven` を作成しておくと、将来バージョンを切り替える際に便利です。<br>
```
sudo tar -xzvf apache-maven-3.9.9-bin.tar.gz -C /opt/
sudo ln -s /opt/apache-maven-3.9.9 /opt/maven
```
Mavenプロジェクトの作成
```
mvn archetype:generate -DgroupId=com.example -DartifactId=my-app -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```
bashでDockerコンテナを終了し、インストールしたものを保存するには以下の手順を実行します。<br>

---
### Javaの実行
Javaの実行はHelloWorldで確認します。

#### **1. ソースコードの準備**
まず、以下の内容で`HelloWorld.java`という名前のファイルを作成します。
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```
#### **2. コンパイル**
Javaソースコードをコンパイルしてバイトコード（`.class`ファイル）を生成します。

##### **コマンド**
```bash
javac HelloWorld.java
```

#### **3. 実行**
コンパイルされたクラスファイル（`.class`）を実行します。

##### **コマンド**
```bash
java HelloWorld
```

#### **4. 実行環境の確認**
以下の環境が整っていることを確認してください。

1. **JDKがインストールされていること**:
   - 確認コマンド：
     ```bash
     java -version
     javac -version
     ```
   - 正常にインストールされていれば、バージョン情報が表示されます。

2. **Javaのパス設定**:
   - 環境変数`JAVA_HOME`が正しく設定されている必要があります。
   - 必要に応じて以下のコマンドでパスを設定してください：
     ```bash
     export JAVA_HOME=/path/to/jdk
     export PATH=$JAVA_HOME/bin:$PATH
     ```
#### **5. 注意点**
- **ファイル名とクラス名が一致していること**:
  ファイル名が`HelloWorld.java`でない場合、コンパイルエラーになります。
  
- **カレントディレクトリの確認**:
  コンパイル・実行時には、ターミナルでファイルが保存されているディレクトリに移動しておく必要があります。
  ```bash
  cd /path/to/your/file
  ```
---

### MavenプロジェクトでJavaをコンパイルして実行するには、以下の手順を実行します。
PDFBoxはMavenプロジェクトで環境を作って実行する。
#### **1. Mavenプロジェクトの作成**
Mavenプロジェクトを作成するには、以下のコマンドを使用します。

```bash
mvn archetype:generate -DgroupId=com.example -DartifactId=myapp -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```
- `groupId`: プロジェクトのグループ名（例: `com.example`）。
- `artifactId`: プロジェクト名（例: `myapp`）。

このコマンドを実行すると、以下のようなディレクトリ構造が作成されます：
```
myapp/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/App.java
│   └── test/
│       ├── java/
│       │   └── com/example/AppTest.java
```

---

#### **2. Javaコードを記述**
`src/main/java/com/example/App.java`に以下のようなコードを書きます。
PDFBoxを使ってpdfファイルのテキスト抽出を行います。

```java
package com.example;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;

public class App {
    public static void main(String[] args) {
       File file = new File("/app/myapp/example.pdf");
       try {
	    PDDocument document = Loader.loadPDF(file);
	    PDFTextStripper textStripper = new PDFTextStripper();
	    String text = textStripper.getText(document);
            System.out.println("Extrated Text:");
            System.out.println(text);
	    document.close();
        } catch (IOException e) {
            System.err.println("Error loading or processing the PDF file.");
	    e.printStackTrace();
        }
    }
}
```

#### **3. `pom.xml`の設定**
`pom.xml`にJavaバージョンや依存関係を設定します。

###### **基本的な設定例**
Mavenで直接Javaコードを実行するには、`exec-maven-plugin`を使用します。

<!-- open属性なし -->
<details><summary>サンプル</summary>

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>myapp</artifactId>
  <packaging>jar</packaging>
  <version>1.0-SNAPSHOT</version>
  <name>myapp</name>
  <url>http://maven.apache.org</url>
  <build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version> <!-- 最新バージョンを指定 -->
            <configuration>
                <source>11</source> <!-- 使用するJavaバージョン -->
                <target>11</target>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.codehaus.mojo</groupId>
            <artifactId>exec-maven-plugin</artifactId>
            <version>3.0.0</version>
            <configuration>
                <mainClass>com.example.App</mainClass>
            </configuration>
        </plugin>
	<plugin>
           <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <version>3.2.2</version>
            <configuration>
                <archive>
		  <manifest>
		    <mainClass>com.example.App</mainClass>
		  </manifest>
		</archive>
            </configuration>
        </plugin>
    </plugins>
</build>
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>
    </dependency>
   <dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.4</version>
   </dependency>
  </dependencies>
</project>
```
</details>


##### ファイルのコピー
MacのローカルフォルダからDockerコンテナ内にファイルをコピーするには、`docker cp`コマンドを使用します。以下は具体的なコマンド例です。

---

##### **コマンド構文**
```bash
docker cp [ローカルのファイルパス] [コンテナ名]:[コンテナ内のコピー先パス]
```

---

##### **具体例**
例えば、Macのローカルフォルダにある`~/Documents/sample.txt`というファイルを、Dockerコンテナ`my-container`内の`/app`ディレクトリにコピーする場合：

```bash
docker cp ~/Documents/sample.txt my-container:/app/sample.txt
```

- **`~/Documents/sample.txt`**: ローカルのコピー元ファイル。
- **`my-container`**: コピー先のDockerコンテナ名またはID。
- **`/app/sample.txt`**: コンテナ内のコピー先パス。

---
- コピー元がディレクトリの場合、指定したフォルダ全体がそのままコピーされます。
---

##### **注意点**
1. **コンテナが実行中でなくてもコピー可能**  
   `docker cp`はコンテナが停止中でも使用できます。   
---
これで、MacからDockerコンテナへのファイルコピーが簡単に実行できます！


#### **4. Javaコードをコンパイル**
以下のコマンドでプロジェクトをコンパイルします：

```bash
mvn compile
```
- コンパイルが成功すると、生成されたクラスファイルは`target/classes`ディレクトリに保存されます。
```
bash-4.2# ls target/classes/com/example/App.class
target/classes/com/example/App.class
```

#### **5. Javaコードを実行**
##### **実行コマンド**
以下のコマンドでJavaコードを実行します：
```bash
mvn exec:java
```
### mvnが見つからない時
Mavenがインストールされているか確認。
```
which mvn
```
Mavenが正しくインストールされている場合でも、環境変数 `PATH` や `JAVA_HOME` が正しく設定されていないと動作しません。`PATH` に Maven の実行ファイルディレクトリ（例: `/usr/share/maven/bin`）が含まれているか確認します：
```
echo $PATH
```
含まれていなければ、以下を実行。
```
export PATH=/usr/share/maven/bin:$PATH
```
Mavenを `/opt` にインストールし、プロジェクトディレクトリ `/app/myapp` で使用できるようにするには、以下の手順を実施してください。

#### **1. 環境変数を設定**
Mavenをコマンドラインで使用するために、環境変数 `PATH` に Maven の `bin` ディレクトリを追加します。

##### **手順**
1. `/opt/apache-maven-3.9.9/bin` を `PATH` に追加：
   ```bash
   export PATH=/opt/apache-maven-3.9.9/bin:$PATH
   ```
   - このコマンドを実行すると、現在のセッションで Maven が使用可能になります。

2. 永続的に設定する場合は、以下のファイルに記述します：
   - **`~/.bashrc`** または **`~/.bash_profile`** を編集：
     ```bash
     echo 'export PATH=/opt/apache-maven-3.9.9/bin:$PATH' >> ~/.bashrc
     ```
   - 設定を反映させる：
     ```bash
     source ~/.bashrc
     ```

3. 設定が正しく反映されたか確認：
   ```bash
   mvn -v
   ```
   - 出力例：
     ```
     Apache Maven 3.9.9 (cecedd343002696d0abb50b32b541b8a6ba2883f)
     Maven home: /opt/apache-maven-3.9.9
     Java version: 11.0.16, vendor: Oracle Corporation
     Java home: /usr/lib/jvm/java-11-openjdk-amd64
     ```

#### **2. Java環境を確認**
MavenはJavaを必要とするため、Javaがインストールされているか確認し、必要であれば設定します。

##### **手順**
1. Javaのインストール状況を確認：
   ```bash
   java -version
   ```
   - 正常な出力例：
     ```
     java version "11.0.16" 2025-01-15 LTS
     Java(TM) SE Runtime Environment (build 11.0.16+8-LTS)
     Java HotSpot(TM) 64-Bit Server VM (build 11.0.16+8-LTS, mixed mode)
     ```

2. `JAVA_HOME` を設定：
   - Javaのインストールパスを確認：
     ```bash
     readlink -f $(which java) | sed "s:/bin/java::"
     ```
   - 環境変数に追加：
     ```bash
     export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
     export PATH=$JAVA_HOME/bin:$PATH
     ```

3. 永続的に設定する場合は、以下のファイルに記述します：
   - **`~/.bashrc`** または **`~/.bash_profile`** を編集：
     ```bash
     echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64' >> ~/.bashrc
     echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
     ```
   - 設定を反映させる：
     ```bash
     source ~/.bashrc
     ```
     
#### **3. プロジェクトディレクトリで Maven を使用**
プロジェクトディレクトリ `/app/myapp` に移動して Maven コマンドを実行します。

##### **手順**
1. プロジェクトディレクトリへ移動：
   ```bash
   cd /app/myapp
   ```

2. Mavenコマンドを実行してプロジェクトをビルド：
   ```bash
   mvn compile
   ```
   - 正常な出力例：
     ```
     [INFO] Scanning for projects...
     [INFO] ------------------------------------------------------------------------
     [INFO] BUILD SUCCESS
     [INFO] ------------------------------------------------------------------------
     ```

---

#### **4. POMファイルの確認**
Mavenプロジェクトには `pom.xml` が必要です。このファイルにはプロジェクトの設定が記述されています。

##### **手順**
1. プロジェクトディレクトリに `pom.xml` が存在するか確認：
   ```bash
   ls /app/myapp/pom.xml
   ```

2. 存在しない場合は新しいプロジェクトを作成：
   ```bash
   mvn archetype:generate \
       -DgroupId=com.example \
       -DartifactId=myapp \
       -DarchetypeArtifactId=maven-archetype-quickstart \
       -DinteractiveMode=false
   ```

---

#### **5. トラブルシューティング**
##### **a. Mavenコマンドが見つからない場合**
- 環境変数 `PATH` が正しく設定されていない可能性があります。
- 再度以下のコマンドで確認してください：
  ```bash
  echo $PATH | grep apache-maven-3.9.9/bin
  ```
##### **b. Javaエラーが発生する場合**
- `JAVA_HOME` が正しく設定されていない可能性があります。
- 以下のコマンドで確認してください：
  ```bash
  echo $JAVA_HOME
  ```

DockerでAmazon Corretto 11を使用している場合、Linux自体のアップデートは通常必要ありません。
### **Amazon Correttoのアップデート**
- Amazon Correttoは四半期ごとにアップデートが提供されます。セキュリティ修正やバグ修正はこのアップデートに含まれています。
- 最新バージョンを利用するには、新しいDockerイメージを取得して再構築するだけで十分です。

##### 最新イメージ取得コマンド:
```bash
docker pull amazoncorretto:11
```

##### 古いコンテナを削除して新しいコンテナを起動:
```bash
docker stop <container_id>
docker rm <container_id>
docker run -it --name my-corretto amazoncorretto:11 /bin/bash
```

#### **コンテナを終了する**
Dockerコンテナを終了するには、まずコンテナから抜けます。

##### **方法**
1. **コンテナ内で`exit`コマンドを実行**:
   ```bash
   exit
   ```
   - これにより、コンテナのセッションから抜け出し、ホスト環境に戻ります。
   - コンテナは停止状態になります。

2. **コンテナを明示的に停止**（必要に応じて）:
   ```bash
   docker stop <CONTAINER_IDまたはNAME>
   ```
   - 起動中のコンテナIDや名前は以下のコマンドで確認できます。
     ```bash
     docker ps
     ```
#### **コンテナの状態を保存する**
変更した内容やインストールしたパッケージを保存するために、現在のコンテナ状態をDockerイメージとして保存します。

##### **手順**
1. **現在のコンテナIDまたは名前を確認**:
   ```bash
   docker ps -a
   ```
2. **`docker commit`でイメージとして保存**:
   ```bash
   docker commit <CONTAINER_ID> <REPOSITORY_NAME>:<TAG>
   ```
   - 例:
     ```bash
     docker commit abc123def456 my_saved_image:latest
     ```
   - `my_saved_image`という名前で、現在のコンテナ状態がイメージとして保存されます。

3. **保存したイメージを確認**:
   ```bash
   docker images
   ```
   - 出力例:
     ```
     REPOSITORY       TAG       IMAGE ID       CREATED          SIZE
     my_saved_image   latest    789abc123def   10 seconds ago   200MB
     ```
#### **保存したイメージから再利用**
保存したイメージを使って新しいコンテナを作成できます。

##### **手順**
1. 新しいコンテナを作成して起動:
   ```bash
   docker run -it --name <NEW_CONTAINER_NAME> <REPOSITORY_NAME>:<TAG>
   ```
2. コンテナ内で変更が反映されていることを確認します。


Dockerコンテナを再起動するには、以下の方法を使用します。
### **再起動コマンド**
Dockerコンテナを再起動するには、`docker restart`または`docker container restart`コマンドを使用します。

##### **すべてのコンテナ（停止中も含む）の名前を確認**
```bash
docker ps -a --format '{{.Names}}'
```

##### **コマンド例**
```bash
docker restart <コンテナ名またはコンテナID>
```
または
```bash
docker container restart <コンテナ名またはコンテナID>
```

#### **コンテナの状態確認**
再起動前後にコンテナの状態を確認するには、以下のコマンドを使用します。

##### **実行中のコンテナ一覧**
```bash
docker ps
```

##### **すべてのコンテナ一覧（停止中も含む）**
```bash
docker ps -a
```

Dockerコンテナを再起動した後、ターミナルから`bash`を使うには、以下の手順を実行します。
#### **コンテナ内で`bash`を使用する**
再起動したコンテナ内で`bash`を使用するには、`docker exec`コマンドを使います。
```bash
docker exec -it <コンテナ名またはコンテナID> /bin/bash
```
