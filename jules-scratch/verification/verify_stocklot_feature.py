from playwright.sync_api import Page, expect, sync_playwright, TimeoutError

def run(playwright):
    browser = playwright.chromium.launch(headless=True)
    context = browser.new_context()
    page = context.new_page()

    try:
        # Navigate to the app
        page.goto("http://localhost:5173/")
        page.wait_for_load_state('networkidle')

        # Click on the "ロット管理" link using its text content
        page.get_by_text("ロット管理").click()

        # Wait for the table to be visible
        stock_lot_table = page.locator("table.common-table")
        expect(stock_lot_table).to_be_visible(timeout=10000)

        # Get the initial row count
        initial_row_count = stock_lot_table.locator("tbody tr").count()

        # --- Test Add ---
        page.get_by_role("button", name="新規購入").click()

        add_form = page.locator("div.form-container")
        expect(add_form).to_be_visible()

        # Wait for the owner dropdown to be populated
        owner_select = add_form.locator("select").first
        expect(owner_select).to_be_visible()
        page.wait_for_selector('select:first-of-type option:nth-child(2)')
        owner_select.select_option(index=1)

        # Wait for the stock dropdown to be populated
        stock_select = add_form.locator("select").last
        expect(stock_select).to_be_visible()
        page.wait_for_selector('select:last-of-type option:nth-child(2)')
        stock_select.select_option(index=1)

        unit_input = add_form.locator("input[type='number']")
        expect(unit_input).to_be_visible()
        unit_input.fill("5")

        nisa_checkbox = add_form.locator("input[type='checkbox']")
        expect(nisa_checkbox).to_be_visible()
        nisa_checkbox.check()

        add_form.get_by_role("button", name="Save").click()

        # Wait for the form to disappear
        expect(add_form).not_to_be_visible()

        # Verify the new row is in the table
        expect(stock_lot_table.locator("tbody tr")).to_have_count(initial_row_count + 1)

        # Get the first row for editing and deleting
        first_row = stock_lot_table.locator("tbody tr").first

        # --- Test Edit ---
        # Get original values
        original_unit_text = first_row.locator("td").nth(2).inner_text()
        original_unit = int(original_unit_text)

        # Click the edit button
        first_row.get_by_role("button", name="Edit").click()

        # Wait for the edit form to appear
        edit_form = page.locator("div.form-container")
        expect(edit_form).to_be_visible()

        # Update the unit value
        unit_input = edit_form.locator("input[type='number']")
        new_unit = original_unit + 1
        unit_input.fill(str(new_unit))

        # Toggle the NISA checkbox
        nisa_checkbox = edit_form.locator("input[type='checkbox']")
        nisa_checkbox.check()

        # Save the changes
        edit_form.get_by_role("button", name="Save").click()

        # Wait for the form to disappear
        expect(edit_form).not_to_be_visible()

        # Verify the update in the table
        expect(first_row.locator("td").nth(2)).to_have_text(str(new_unit))
        expect(first_row.locator("td").nth(4)).to_have_text("Yes")

        # --- Test Delete ---
        # Set up a dialog handler to automatically confirm deletion
        page.once("dialog", lambda dialog: dialog.accept())

        # Click the delete button
        first_row.get_by_role("button", name="Delete").click()

        # Verify the row count has decreased by one
        expect(stock_lot_table.locator("tbody tr")).to_have_count(initial_row_count)

        # Take a screenshot for verification
        page.screenshot(path="jules-scratch/verification/verification.png")

    except TimeoutError as e:
        print(f"A timeout error occurred: {e}")
        page.screenshot(path="jules-scratch/verification/error.png")
        raise

    finally:
        # Clean up
        context.close()
        browser.close()

with sync_playwright() as playwright:
    run(playwright)