### **ロット識別の必要性**
日本の株式市場では100株が最小単元であるため、200株購入時はstock.minumal_unit(=100) * stock_lot.unit(=2)を設定します 。
各ロットは独立した取得時期を持つため、**ロット別の識別管理**が不可欠です 。
ロットの一部の単元を売却した場合はロット数を減少させます。

### **配当金の個別紐づけ**
配当金を受け取った際は配当履歴レコードを1単元ごとに作成し、各配当履歴レコードにロットIDと紐づけます。
ロット数が減少した場合は、stock_lot_idに紐づく配当履歴のレコードをすべて探し出し、減少したsell_transactionレコードのIDを配当履歴レコードにつける。そしてstock_lot_idを削除する。

### **優待金の個別紐づけ**
配当金(incoming_history)と同じ仕様とします。

## ER図（ロット別管理対応）

```mermaid
erDiagram
    stock {
        UUID id PK
        UUID sector_id FK "セクターID"
        string code UK "銘柄コード"
        string name "銘柄名"
        decimal current_price "現在の価格"
        decimal dividend "1単元配当金"
        decimal minumal_unit "最小単元数"
        timestamp earnings_date "業績発表日"
    }
    
    stock_lot {
        UUID id PK "ロットID"
        UUID owner_id FK "オーナーID"
        UUID stock_id FK "銘柄ID" 
        boolean is_nisa "NISA口座フラグ"
        decimal unit "単元数"
    }
    
    buy_transaction {
        UUID id PK
        UUID owner_id FK "オーナーID"
        UUID stock_id FK "ストックID"
        decimal unit "購入単元数"
        decimal price "単価"
        decimal fee "手数料"
        timestamp transaction_date "取引日"
    }

    sell_transaction {
        UUID id PK
        UUID buy_transaction_id FK "購入取引ID"
        decimal unit "売却単元数"
        decimal price "単価"
        decimal fee "手数料"
        timestamp transaction_date "取引日"
    }

    incoming_history {
        UUID id PK
        UUID stock_lot_id FK "ロットID"
        UUID sell_transaction_id FK "売却取引ID"
        decimal incoming "配当金"
        decimal payment_date "支払日"
    }

    benefit_history {
        UUID id PK
        UUID stock_lot_id FK "ロットID"
        UUID sell_transaction_id FK "売却取引ID"
        decimal benefit "優待金"
        decimal payment_date "支払日"
    }

    owner {
        UUID id PK
        string name UK "オーナー名"
    }

    sector {
        UUID id PK
        string name UK "セクター名"
    }
    owner ||--o{ stock_lot : "保有"
    stock ||--o{ stock_lot : "銘柄"
    stock ||--o{ buy_transaction : "購入履歴"
    stock_lot ||--o{ incoming_history : "配当履歴"
    buy_transaction ||--o{ sell_transaction : "売却履歴"
    sell_transaction_lot ||--o{ incoming_history : "配当履歴"
    stock_lot ||--o{ benefit_history : "優待履歴"
    sell_transaction_lot ||--o{ benefit_history : "優待履歴"
    sector ||--o{ stock : "分類"
    buy_transaction ||--o{ stock_lot : "購入"
```
