from playwright.sync_api import sync_playwright, expect
import sys

def run_verification():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()

        try:
            print("Navigating to the stock lot list page...")
            page.goto("http://localhost:5173/stocklot", timeout=60000)
            print("Navigation to stocklot list complete.")

            print("Clicking the 'New Registration' button...")
            page.get_by_role("button", name="新規登録").click()
            print("Clicked 'New Registration' button.")

            print("Waiting for the add page to load...")
            expect(page).to_have_url("http://localhost:5173/stocklot/add", timeout=10000)
            print("URL is correct.")
            expect(page.get_by_role("heading", name="ストックロット新規登録")).to_be_visible()
            print("Heading is visible.")

            print("Filling out the form using a robust locator strategy...")

            # Helper function to find input in the same row as a label
            def get_input_by_row_label(label_text):
                return page.locator("tr", has_text=label_text).locator("input, select")

            get_input_by_row_label("日付").fill("2025-10-07")
            print("Filled date.")

            get_input_by_row_label("種類").select_option("buy")
            print("Selected type.")

            stock_select = get_input_by_row_label("銘柄")
            expect(stock_select.locator('option')).not_to_have_count(1, timeout=10000) # Wait for options to load (more than just the default)
            stock_select.select_option(index=1)
            print("Selected stock.")

            owner_select = get_input_by_row_label("オーナー")
            expect(owner_select.locator('option')).not_to_have_count(1, timeout=10000) # Wait for options to load
            owner_select.select_option(index=1)
            print("Selected owner.")

            get_input_by_row_label("単元数").fill("100")
            print("Filled units.")
            get_input_by_row_label("価格").fill("12345")
            print("Filled price.")
            get_input_by_row_label("手数料").fill("500")
            print("Filled fees.")

            print("Taking screenshot of the filled form...")
            page.screenshot(path="jules-scratch/verification/add_stock_lot_form.png")
            print("Screenshot taken successfully.")

        except Exception as e:
            print(f"An error occurred during verification: {e}", file=sys.stderr)
            page.screenshot(path="jules-scratch/verification/error.png")
        finally:
            print("Closing browser.")
            browser.close()

if __name__ == "__main__":
    run_verification()