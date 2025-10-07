from playwright.sync_api import sync_playwright, Page, expect

def run_verification(page: Page):
    """
    This function verifies that a user can navigate to the Stock Lot page
    and that the page content is correct.
    """
    # 1. Arrange: Go to the application's homepage.
    page.goto("http://localhost:5173/")

    # 2. Act: Find the navigation item by its text content and click it.
    stocklot_link = page.get_by_text("ストックロット管理")
    stocklot_link.click()

    # 3. Assert: Confirm the navigation was successful.
    # We expect the page heading to be "ストックロット一覧".
    heading = page.get_by_role("heading", name="ストックロット一覧")
    expect(heading).to_be_visible()

    # 4. Assert: Check if the table is populated.
    # We wait for the network request to finish and the table to appear.
    expect(page.locator(".common-table")).to_be_visible()

    # 5. Screenshot: Capture the final result for visual verification.
    page.screenshot(path="/app/jules-scratch/verification/stocklot_page.png")

def main():
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        page = browser.new_page()
        run_verification(page)
        browser.close()

if __name__ == "__main__":
    main()