このシナリオでは、**保有数量が途中で変動する**という点が重要です。前回の`holding`テーブル設計では、1つのレコードが「購入から売却まで」を管理しているため、部分売却という事象に対応できません。

## 損益計算の考え方

---

損益を正確に計算するためには、以下の情報が必要です。

1.  **購入時点**: 何株をいくらで、いつ購入したか。
2.  **売却時点**: 何株をいくらで、いつ売却したか。
3.  **保有時点**: 確定した損益と、未確定（含み）の損益。
4.  **収入**: 配当金、株主優待などの受取履歴。

ご提示のシナリオでは、最初の購入と後続の部分売却で、**平均取得単価**の概念が必要になります。
例えば、200株購入した時の単価と、その後100株を売却した時の単価が異なります。損益を計算するには、どの100株を売却したのかを特定する必要があります（**平均法**や**移動平均法**など）。

## 修正案の具体化

---

この問題を解決するため、前回の提案である**`transaction`テーブル**と**`holding`テーブル**への分割が有効です。

### 1. `transaction`テーブル

すべての取引履歴（購入・売却）を、株数と単価で記録します。

* `id` (PK)
* `holding_id` (FK): どの銘柄の取引か
* `transaction_type`: `buy` または `sell`
* `volume`: 取引数量 (例: 200株、100株)
* `price`: 取引単価
* `tax`: 手数料
* `date`: 取引日

**シナリオのデータ例:**

| `id` | `holding_id` | `transaction_type` | `volume` | `price` | `tax` | `date` |
|---|---|---|---|---|---|---|
| `t1` | `p1` | `buy` | 200 | `X`円 | `Y`円 | `buy_date` |
| `t2` | `p1` | `sell` | 100 | `Z`円 | `W`円 | `sale_date` |

### 2. `holding`テーブル

現在の保有状況を管理します。

* `id` (PK)
* `owner_id` (FK)
* `stock_id` (FK)
* `current_volume`: 現在の保有数量 (この例では100株)
* `average_cost`: 平均取得単価

**このテーブルは、`transaction`テーブルのデータを集計して更新することで、常に最新の状態を保ちます。**

### 3. `income_history`テーブル

配当金や優待の履歴を管理します。

* `id` (PK)
* `holding_id` (FK)
* `income_type`: `dividend` または `preferential`
* `amount`: 受取金額
* `date`: 受取日

**シナリオのデータ例:**

| `id` | `holding_id` | `income_type` | `amount` | `date` |
|---|---|---|---|---|
| `i1` | `p1` | `dividend` | `100`株 × `10`円 | `payment_date` |

---

## 損益の計算

---

上記のテーブル構造を使うと、損益は以下のように計算できます。

* **確定損益**: `transaction`テーブルから`sell`のレコードを取得し、売却単価と平均取得単価の差額から計算します。
    * `確定損益 = (売却単価 - 平均取得単価) × 売却数量 - 売却手数料`

* **含み損益**: `holding`テーブルの**平均取得単価**と**現在の株価**の差額から計算します。
    * `含み損益 = (現在の株価 - 平均取得単価) × 現在の保有数量`

* **配当金・優待金**: `income_history`テーブルから`dividend`や`preferential`のレコードを集計します。

このようにテーブルを分割することで、**購入・売却・保有**という各時点の情報を正確に管理でき、部分売却や配当金の受け取りを考慮した、より複雑な損益計算に対応できます。

### 改良版
```mermaid
erDiagram
    transaction {
        UUID id PK
        UUID holding_id FK "ポジションID"
        ENUM transaction_type "取引タイプ (buy/sell)"
        decimal volume "取引数量"
        decimal price "取引単価"
        decimal tax "手数料"
        timestamp date "取引日"
    }

    holding {
        UUID id PK
        UUID owner_id FK "オーナーID"
        UUID stock_id FK "銘柄ID"
        boolean nisa "NISAかどうか"
        decimal current_volume "現在の保有数量"
        decimal average_price "平均取得単価"
    }

    income {
        UUID id PK
        UUID holding_id FK "ポジションID"
        ENUM income_type "収入タイプ (dividend/preferential)"
        decimal amount "金額"
        timestamp date "受取日"
    }

    owner {
        UUID id PK
        string name UK
    }

    stock {
        UUID id PK
        UUID sector_id FK "セクターID"
        string code "銘柄コード"
        string name "銘柄名"
    }
    
    sector {
        UUID id PK
        string name "セクター名"
    }

    owner ||--o{ holding : "保有する"
    stock ||--o{ holding : "属する"
    holding ||--o{ transaction : "取引履歴"
    holding ||--o{ income : "収入履歴"
    sector ||--o{ stock : "属する"
