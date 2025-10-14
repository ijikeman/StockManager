<template>
  <div class="container mt-4">
    <h2>ロット売却</h2>
    <form @submit.prevent="submitForm">
      <div class="mb-3">
        <label for="stockName" class="form-label">銘柄</label>
        <input type="text" class="form-control" id="stockName" :value="lot.stock.name" disabled>
      </div>
      <div class="mb-3">
        <label for="ownerName" class="form-label">所有者</label>
        <input type="text" class="form-control" id="ownerName" :value="lot.owner.name" disabled>
      </div>
      <div class="mb-3">
        <label for="currentUnit" class="form-label">保有単元数</label>
        <input type="number" class="form-control" id="currentUnit" :value="lot.currentUnit" disabled>
      </div>
      <div class="mb-3">
        <label for="unit" class="form-label">売却単元数</label>
        <input type="number" class="form-control" id="unit" v-model.number="sell.unit" required>
        <div v-if="unitError" class="text-danger">{{ unitError }}</div>
      </div>
      <div class="mb-3">
        <label for="price" class="form-label">売却価格</label>
        <input type="number" class="form-control" id="price" v-model.number="sell.price" required>
      </div>
      <div class="mb-3">
        <label for="fee" class="form-label">手数料</label>
        <input type="number" class="form-control" id="fee" v-model.number="sell.fee" required>
      </div>
      <div class="mb-3">
        <label for="transactionDate" class="form-label">取引日</label>
        <input type="date" class="form-control" id="transactionDate" v-model="sell.transactionDate" required>
      </div>
      <button type="submit" class="btn btn-primary">売却</button>
      <router-link to="/stocklot" class="btn btn-secondary ms-2">キャンセル</router-link>
    </form>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "StockLotSell",
  props: ['id'],
  data() {
    return {
      lot: {
        stock: { name: '' },
        owner: { name: '' },
        currentUnit: 0,
      },
      sell: {
        unit: 0,
        price: 0,
        fee: 0,
        transactionDate: new Date().toISOString().slice(0, 10),
      },
      unitError: ''
    };
  },
  methods: {
    async fetchLot() {
      try {
        const response = await axios.get(`/api/stocklot/${this.id}`);
        this.lot = response.data;
      } catch (error) {
        console.error("Error fetching stock lot:", error);
      }
    },
    validateUnit() {
      if (this.sell.unit > this.lot.currentUnit) {
        this.unitError = '売却単元数が保有単元数を超えています。';
        return false;
      }
      if (this.sell.unit <= 0) {
        this.unitError = '売却単元数は1以上で入力してください。';
        return false;
      }
      this.unitError = '';
      return true;
    },
    async submitForm() {
      if (!this.validateUnit()) {
        return;
      }
      try {
        const payload = {
          unit: this.sell.unit,
          price: this.sell.price,
          fee: this.sell.fee,
          transactionDate: this.sell.transactionDate,
        };
        await axios.post(`/api/stocklot/${this.id}/sell`, payload);
        this.$router.push("/stocklot");
      } catch (error) {
        console.error("Error selling stock lot:", error);
        if (error.response && error.response.data) {
            alert(`エラー: ${error.response.data}`);
        }
      }
    },
  },
  watch: {
    'sell.unit'() {
      this.validateUnit();
    }
  },
  mounted() {
    this.fetchLot();
  },
};
</script>