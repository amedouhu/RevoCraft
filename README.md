<p align="center"><img src="images/revocraft.png" width="64px" height="64px"></p>

<h1 align="center">RevoCraft</h1>

<p align="center">リボ払いで買い物できるBukkitプラグイン...!</p>

## 概要

* RevoCraftは、リボ払いで買い物を楽しむことができるBukkitプラグインです。
* SpigotAPIを使用している為、Bukkit系サーバーで動作します。
* グラフィカルなインタフェースにより、直感的な操作を実現しています。
* コンフィグファイルにより、詳細な部分まで動作をカスタマイズすることができます。

## ドキュメント

(1) コマンド
  
|コマンド|用法|備考|
|---|---|---|
|buy|第一引数にアイテムIDを指定することで、それに該当する結果を得られます。|アイテムの購入GUIの呼び出し|
|sell|第一引数にアイテムIDを指定することで、それに該当する結果を得られます。|アイテムの売却GUIの呼び出し|
|send|第一引数にMCIDを指定することで、それに該当する結果を得られます。|ユーザの送金GUIの呼び出し|

(2) プラグインの仕様

* buyコマンドを使用して購入したアイテムの代金は負債として上書きされます。
* sellコマンドを使用して売却したアイテムの代金は残高として上書きされます。
* RevoCraftをサーバーにインストールすると、ボスバーを使用したカウントバーが表示されるようになります。このカウントが0になったときに、設定された請求額だけ残高から引き落とされます。(設定された請求額よりも負債額が小さい場合、負債額が請求額となります。)
* 引き落としの際に請求額を残高から支払うことが出来なかった(滞納した)場合、そのプレイヤーはBANされます。
* プレイヤーのオンライン及びオフラインに関わらず、負債額の返済が終わるまで残高からの引き落としが行われます。
* 滞納によりBANされたプレイヤーには、sendコマンドを使用して代わりに負債を支払うことが出来ます。(滞納によりBANされたプレイヤーのみがに送金することが出来ます。)送金によって負債額が0になった場合にはその対象のプレイヤーのBANが解除され、再度参加が認められます。
* 引き落としのときにはその時点での負債額から設定した利子率だけ上乗せされます。
* 引き落としのときには請求を行ったプレイヤーに対し、請求書が表示され、請求額や利子を確認できあます。

(3) コンフィグファイル

* RevoCraftをサーバーにインストールした際に、/plugins/RevoCraftフォルダとその中に各種設定ファイルが生成されます。これらを編集することで、動作を変更できます。
* 設定ファイルはソースのクラスごとに生成されます。(設定ファイルを持たないクラスのものは生成されません。)
* 以下の設定ファイルが生成されます。

|ファイル|クラス|備考|
|---|---|---|
|commands.buy.yml|src.main.java.com.amedouhu.revocraft.commands.Buy|buyコマンドで使用される設定です。|
|commands.sell.yml|src.main.java.com.amedouhu.revocraft.commands.Sell|sellコマンドで使用される設定です。|
|gimmicks.count_bar.yml|src.main.java.com.amedouhu.revocraft.gimmicks.CountBar|カウントバーで使用される設定です。|
|gimmicks.tab_list.yml|src.main.java.com.amedouhu.revocraft.gimmicks.TabList|タブリストで使用される設定です。|
|utils.price.yml|src.main.java.com.amedouhu.revocraft.utils.Price|Priceオブジェクトで使用される設定です。|
|utils.users.yml|src.main.java.com.amedouhu.revocraft.utils.User|Userオブジェクトで使用される設定です。ユーザに紐づいたデータが保存されています。|

* commands.buy.ymlの設定項目

|設定項目|データ型|備考|
|---|---|--|
|max_debt|int|負債の最大額を設定します。オーバーフローが発生しないよう、0~2147483647の範囲で指定してください。|

* commands.sell.ymlの設定項目

|設定項目|データ型|備考|
|---|---|--|
|max_balance|int|残高の最大額を設定します。オーバーフローが発生しないよう、0~2147483647の範囲で指定してください。|

* gimmicks.count_bar.yml

|設定項目|データ型|備考|
|---|---|--|
|count|int|カウントバーで何秒間のカウントを行うか設定します。|
|request|int|引き落としのときに、請求される最大値を設定します。|
|interest|double|利子率を設定します。|
|output|boolean|引き落としのときに、それによってBANされたプレイヤー一覧をチャットに出力するかを設定します。誰もBANされていない場合、出力は行われません。|
|color|String|カウントバーの色を設定します。SpigotAPIのBarColorオブジェクトに存在する値を指定する必要があります。|
|style|String|カウントバーのスタイルを設定します。SpigotAPIのBarStyleオブジェクトに存在する値を指定する必要があります。|
|title|String|カウントバーに表示するタイトルを設定します。${score}を埋め込むことで、スコアを出力できます。|
|kick|String|引き落としのときにBANリストに追加し、KICKを行う際のメッセージを設定します。|
|ban|String|引き落としのときにBANリストに追加する際に使用するメッセージを設定します。|

* gimmicks.tab_list.yml

|設定項目|データ型|備考|
|---|---|--|
|$default|String|タブリスト(プレイヤーリスト)での表示名を設定します。${debt}で負債額${balance}で残高${uuid}でUUID、${mcid}でMCIDを埋め込みます。|

* utils.price.yml

|設定項目|データ型|備考|
|---|---|--|
|STONE|String|アイテムに紐づいたデータを設定します。サブキーのbuy(int)で販売額、sell(int)で買取額を設定します。SpigotAPIのSTONEはMaterialオブジェクトに存在する値を指定する必要があります。|
|...|...|...|

* utils.users.yml

|設定項目|データ型|備考|
|---|---|--|
|d7e5b662-531d-487d-bb30-0c829140e639|String|識別用のUUIDを設定します。サブキーのbalanceで残高、debtで負債、mcidでMCIDを設定します。|
|...|...|...|

* 設定方法として、${example}を使用して値を埋め込む方法を紹介している項目がありますが、これはStringオブジェクトのreplace()とソース内のutilsパッケージを使用しているものであり、疑似的なものです。全ての場所で使用できるのではなく、記述されている範囲のみで使用できます。
* 設定ファイルに記述ミスがある場合、エラー(例外)または該当箇所の処理の停止が発生します。

## ライセンス

* 本ソフトウェアは、Asl( https://github.com/amedouhu/Asl/ )のもと使用できます。
* ©amedouhu